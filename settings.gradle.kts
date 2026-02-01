rootProject.name = "messaging-platform"

// Convention Plugins
includeBuild("build-logic")

// ============================================
// Core - 핵심 도메인 (외부 의존 X)
// 각 발송 미디어의 Bounded Context 분리
// ============================================
include(
    "message-core:partner-domain",
    "message-core:report-domain",
    "message-core:sms-domain",
    "message-core:kakao-domain",
    "message-core:rcs-domain",
    "message-core:naver-domain"
)

// ============================================
// Infrastructure - 기술 구현체
// ============================================
include(
    "message-infrastructure:db-sms",
    "message-infrastructure:db-kakao",
    "message-infrastructure:db-rcs",
    "message-infrastructure:db-naver",
    "message-infrastructure:rabbitmq",
    "message-infrastructure:netty"
)

// ============================================
// Library - 횡단 관심사
// ============================================
include(
    "message-library:id-generator",
    "message-library:logging",
    "message-library:webhook"
)

// ============================================
// UseCase - 애플리케이션 서비스 (비즈니스 로직)
// ============================================
include(
    "message-usecase:sms-usecase",
    "message-usecase:lms-mms-usecase",
    "message-usecase:rcs-usecase",
    "message-usecase:kakao-alimtalk-usecase",
    "message-usecase:kakao-brandmessage-usecase",
    "message-usecase:naver-usecase"
)

// ============================================
// Platform - 외부 플랫폼 연동 (Adapter)
// ============================================
include(
    "message-platform:skt-platform",
    "message-platform:kt-platform",
    "message-platform:lgt-platform",
    "message-platform:kakao-platform",
    "message-platform:kakao-direct-platform",
    "message-platform:naver-platform",
    "message-platform:rcs-platform"
)


// ============================================
// Bootstrap - 앱 실행 (의존성 조립)
// 각 미디어별 독립 배포 단위
// ============================================
include(
    // SMS
    "message-bootstrap:sms-receiver",
    "message-bootstrap:sms-sender",

    // LMS/MMS
    "message-bootstrap:lms-mms-receiver",
    "message-bootstrap:lms-mms-sender",

    // RCS
    "message-bootstrap:rcs-receiver",
    "message-bootstrap:rcs-sender",
    "message-bootstrap:rcs-webhook",

    // 카카오
    "message-bootstrap:kakao-receiver",           // 알림톡 + 브랜드메시지 공통 수신
    "message-bootstrap:kakao-alimtalk-sender",    // 알림톡 발송
    "message-bootstrap:kakao-brandmessage-sender", // 브랜드메시지 발송

    // 네이버
    "message-bootstrap:naver-receiver",
    "message-bootstrap:naver-sender",

    // 리포터 (공통)
    "message-bootstrap:reporter"
)
