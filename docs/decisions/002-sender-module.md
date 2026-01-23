# Sender 모듈 설계 결정사항

## 기술 스택

| 항목 | 선택 | 이유 |
|------|------|------|
| MQ | RabbitMQ | 회사 표준, DLQ/지연메시지 지원 |
| DB | PostgreSQL | JSONB 지원, 통계 쿼리 강점 |
| ORM | Spring Data R2DBC | WebFlux 완전 비동기 |
| 동시성 | 오토스케일링 | Stateless 설계, K8s HPA 대응 |

## 메시지 상태 흐름

```
PENDING → SENDING → SUCCESS
                  → FAILED
                  → EXPIRED
```

## 아키텍처

```
[RabbitMQ: message.queue]
         ↓
   MessageConsumer (prefetch=10)
         ↓
   MessageService
         ↓
   ┌─────┴─────┐
   ↓           ↓
[DB 저장]  [ProviderRouter]
              ↓
         [벤더 API 호출]
              ↓
   ┌──────────┼──────────┐
   ↓          ↓          ↓
 성공       실패       에러
   ↓          ↓          ↓
SUCCESS   재시도?    재시도?
   ↓       Y↓  N↓     Y↓  N↓
   ↓    retry.q FAILED retry.q FAILED
   ↓          ↓          ↓
[result.queue 발행]
```

## RabbitMQ 구성

### Exchange
| 이름 | 타입 | 용도 |
|------|------|------|
| message.exchange | Direct | 발송 요청 |
| result.exchange | Direct | 발송 결과 |
| retry.exchange | Direct | 재시도 (TTL+DLX) |

### Queue
| 이름 | 용도 | DLX 설정 |
|------|------|----------|
| message.queue | 발송 대기 | → message.dlq |
| result.queue | 결과 전달 (reporter 수신) | - |
| retry.queue | 재시도 대기 | → message.queue |
| message.dlq | 최종 실패 메시지 | - |

## 재시도 정책

| 항목 | 값 |
|------|-----|
| 최대 재시도 | 5회 |
| 백오프 방식 | 지수 백오프 |
| 지연 시간 | 1초 → 2초 → 4초 → 8초 → 16초 |
| 실패 후 처리 | DLQ 이동 + FAILED 상태 |

## DB 스키마 (PostgreSQL)

```sql
CREATE TABLE messages (
    id BIGSERIAL PRIMARY KEY,
    message_id VARCHAR(20) NOT NULL UNIQUE,
    partner_id VARCHAR(50) NOT NULL,
    client_msg_id VARCHAR(100),
    type VARCHAR(20) NOT NULL,
    carrier VARCHAR(10),
    recipient VARCHAR(20) NOT NULL,
    content TEXT NOT NULL,
    detail JSONB DEFAULT '{}',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    retry_count INT NOT NULL DEFAULT 0,
    result_code VARCHAR(10),
    result_message VARCHAR(500),
    sent_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_message_partner_client_msg
        UNIQUE (partner_id, client_msg_id)
);
```

## 핵심 컴포넌트

### MessageConsumer
- RabbitMQ에서 메시지 수신 (Reactor RabbitMQ)
- prefetch로 동시 처리량 조절
- Manual ACK로 안정적 처리

### MessageService
- 멱등성 보장 (clientMsgId 중복 체크)
- 상태 전이 관리
- Provider 호출 + 결과 처리
- 재시도 스케줄링

### ResultPublisher
- 발송 결과를 result.queue에 발행
- 재시도 메시지를 retry.queue에 발행 (TTL 설정)

## 설정값

```yaml
sender:
  consumer:
    prefetch: 10
  retry:
    max-attempts: 5
    initial-delay-ms: 1000
    multiplier: 2.0
```

## 결과 처리 흐름

1. **발송 성공**
   - DB: status=SUCCESS, resultCode, sentAt 업데이트
   - result.queue에 결과 발행

2. **발송 실패 (재시도 가능)**
   - retry_count 증가
   - retry.queue에 TTL과 함께 발행
   - TTL 만료 시 message.queue로 재전달

3. **발송 실패 (최종)**
   - DB: status=FAILED 업데이트
   - result.queue에 결과 발행
   - (nack 시 DLQ로 이동)
