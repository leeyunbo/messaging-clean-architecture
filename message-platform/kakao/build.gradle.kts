plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    `java-library`
}

// usecase:kakao와 이름 충돌 방지
group = "com.messaging.platform"

dependencies {
    // 카카오 도메인 의존
    implementation(project(":message-core:kakao-domain"))
    implementation(project(":message-infrastructure:webclient"))
    implementation(project(":message-library:logging"))
    implementation(project(":message-library:id-generator"))

    // Spring WebFlux
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    // Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    // Resilience4j
    implementation("io.github.resilience4j:resilience4j-circuitbreaker:2.2.0")
    implementation("io.github.resilience4j:resilience4j-reactor:2.2.0")

    // Jackson
    implementation("tools.jackson.core:jackson-databind:3.0.0")
    implementation("tools.jackson.module:jackson-module-kotlin:3.0.0")
}
