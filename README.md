# 메시지 발송 플랫폼

## 개요
발송 미디어 별로 분산된 메시지 발송 시스템을 통합하되, 각 발송 미디어의 독립적 운영을 유지하기 위해 클린 아키텍처 기반 모노레포로 설계
- 통합: 공통 도메인/인프라 코드 공유
- 독립: 채널별 배포 단위 분리

**참고)** 아키텍처 설계가 중요한 토이 프로젝트이며 회사의 업무와 관련되어 있기에, 상세한 로직은 생략하고 구조 중심으로 구현하였음

## 패키지 구조

| 레이어 | 설명 |
|--------|------|
| `message-core/` | 도메인 모델, 포트 인터페이스 |
| `message-usecase/` | 비즈니스 로직 |
| `message-platform/` | 외부 API 어댑터 |
| `message-infrastructure/` | 기술 구현 (RabbitMQ, DB, WebClient) |
| `message-library/` | 공통 유틸리티 |
| `message-bootstrap/` | 실행 애플리케이션 |

## 의존성 방향

```
bootstrap → usecase → core ← platform
              ↓         ↑
         infrastructure
```

- `core`: 순수 도메인, 외부 의존성 없음
- `usecase`: core의 포트 인터페이스 사용
- `platform`, `infrastructure`: core의 포트 구현
- `bootstrap`: 모든 모듈 조립

## 기술 스택

Kotlin 2.3 / Spring Boot 4.0 / WebFlux / Spring AMQP / R2DBC / Resilience4j
