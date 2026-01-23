rootProject.name = "messaging-platform"

// ============================================
// Core - 핵심 도메인 (외부 의존 X)
// 각 발송 미디어의 Bounded Context 분리
// ============================================
include(
    "core:partner-domain",
    "core:report-domain",
    "core:sms-domain",
    "core:kakao-domain",
    "core:rcs-domain",
    "core:naver-domain"
)

// ============================================
// Infrastructure - 기술 구현체
// ============================================
include(
    "infrastructure:db",
    "infrastructure:rabbitmq",
    "infrastructure:webclient",
    "infrastructure:netty"
)

// ============================================
// Library - 횡단 관심사
// ============================================
include(
    "library:id-generator",
    "library:logging"
)

// ============================================
// UseCase - 애플리케이션 서비스 (비즈니스 로직)
// ============================================
include(
    "usecase:sms",
    "usecase:lms-mms",
    "usecase:rcs",
    "usecase:kakao",          // 알림톡 (Kakao)
    "usecase:kakao-direct",   // 브랜드메시지 (Kakao Direct)
    "usecase:naver"
)

// ============================================
// Platform - 외부 플랫폼 연동 (Adapter)
// ============================================
include(
    "platform:skt",
    "platform:kt",
    "platform:lgt",
    "platform:kakao",          // 알림톡 (Kakao)
    "platform:kakao-direct",   // 브랜드메시지 (Kakao Direct)
    "platform:naver"
)


// ============================================
// Bootstrap - 앱 실행 (의존성 조립)
// 각 미디어별 독립 배포 단위
// ============================================
include(
    // SMS
    "bootstrap:sms-receiver",
    "bootstrap:sms-sender",

    // LMS/MMS
    "bootstrap:lms-mms-receiver",
    "bootstrap:lms-mms-sender",

    // RCS
    "bootstrap:rcs-receiver",
    "bootstrap:rcs-sender",

    // 카카오
    "bootstrap:kakao-receiver",           // 알림톡 + 브랜드메시지 공통 수신
    "bootstrap:kakao-alimtalk-sender",    // 알림톡 발송
    "bootstrap:kakao-brandmessage-sender", // 브랜드메시지 발송

    // 네이버
    "bootstrap:naver-receiver",
    "bootstrap:naver-sender",

    // 리포터 (공통)
    "bootstrap:reporter"
)
