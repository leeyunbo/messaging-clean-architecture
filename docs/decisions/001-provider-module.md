# Provider 모듈 설계 결정사항

## 프로젝트 구조

```
messaging-platform/
├── common/          # 공통 도메인 모델
├── provider/        # 벤더 API 호출 (현재 완료)
├── sender/          # MQ 수신 → 발송 처리
├── receiver/        # HTTP API → MQ 발행
└── reporter/        # 결과 조회/웹훅
```

## 기술 스택

| 항목 | 선택 | 이유 |
|------|------|------|
| 언어 | Kotlin | Null safety, DSL, 간결한 문법 |
| 프레임워크 | Spring WebFlux | 비동기 논블로킹, 높은 처리량 |
| HTTP 클라이언트 | WebClient | Reactor 기반, 비동기 지원 |
| JSON | Jackson 3.x | Kotlin 모듈 지원 |
| 장애 격리 | Resilience4j | 서킷브레이커, 재시도 |
| 테스트 | JUnit5 + MockWebServer | 표준 + HTTP 모킹 |

## 도메인 모델

### MessageType (메시지 유형)
```kotlin
enum class MessageType {
    SMS,                  // 단문
    LMS,                  // 장문
    MMS,                  // 멀티미디어
    RCS,                  // Rich Communication
    KAKAO_ALIMTALK,       // 카카오 알림톡
    KAKAO_BRAND_MESSAGE,  // 카카오 브랜드메시지
    NAVER_TALK            // 네이버톡
}
```

### Carrier (통신사)
```kotlin
enum class Carrier {
    SKT, KT, LGT
}
// 카카오/네이버 등 통신사 무관 채널은 carrier = null
```

## Provider 구조

### 인터페이스
```kotlin
interface MessageProvider {
    fun supportedTypes(): Set<MessageType>
    fun supportedCarrier(): Carrier?
    fun send(request: SendRequest): Mono<SendResult>
}
```

### 구현체 (15개)
| 채널 | SKT | KT | LGT |
|------|-----|----|----|
| SMS | SktSmsProvider | KtSmsProvider | LgtSmsProvider |
| LMS/MMS | SktLmsMmsProvider | KtLmsMmsProvider | LgtLmsMmsProvider |
| RCS | SktRcsProvider | KtRcsProvider | LgtRcsProvider |

| 채널 | Provider |
|------|----------|
| 카카오 알림톡 | KakaoAlimtalkProvider |
| 카카오 브랜드메시지 | KakaoBrandMessageProvider |
| 네이버톡 | NaverTalkProvider |

### 라우팅
```kotlin
class ProviderRouter(providers: List<MessageProvider>) {
    fun send(request: SendRequest): Mono<SendResult>
    fun getProvider(type: MessageType, carrier: Carrier?): MessageProvider?
}
// MessageType + Carrier 조합으로 적절한 Provider 선택
```

## 에러 처리 전략

### 에러 코드 체계
| 코드 | 상황 | retryable |
|------|------|-----------|
| HTTP_4xx | 클라이언트 에러 | false |
| HTTP_5xx | 서버 에러 | true |
| TIMEOUT | 타임아웃 | true |
| CONNECTION_ERROR | 연결 실패 | true |
| CIRCUIT_OPEN | 서킷 열림 | true |
| P001 | Provider 없음 | false |

### 재시도 정책
- 연결 실패 시 최대 3회 재시도 (기본값)
- 지수 백오프 적용
- 서킷브레이커: 실패율 50% 초과 시 OPEN

## 테스트 전략

### 커버리지
- Line Coverage: 96%
- 모든 Provider 성공/실패 케이스 검증

### 테스트 헬퍼
```kotlin
// MockResponse 생성
mockSuccess(body), mockHttpError(statusCode), mockTimeout(), mockDisconnect()

// Provider별 응답
SktResponse.success(), KtResponse.error(), KakaoResponse.success()

// SendRequest DSL
smsRequest { messageId = "test-001" }
kakaoAlimtalkRequest { recipient = "01012345678" }

// Assertion 확장 함수
result.shouldBeSuccess()
result.shouldBeFail(expectedCode = "E001")
result shouldHaveCode "TIMEOUT"
```

## 설정값

| 항목 | 기본값 |
|------|--------|
| 타임아웃 | 5초 |
| 최대 재시도 | 3회 |
| 서킷브레이커 실패율 임계치 | 50% |
| 서킷브레이커 슬라이딩 윈도우 | 10 |
| 서킷브레이커 OPEN 대기 시간 | 60초 |
