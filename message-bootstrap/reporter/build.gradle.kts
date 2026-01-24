plugins {
    kotlin("jvm")
    kotlin("plugin.spring")

}

dependencies {
    // Core - Reporter는 모든 도메인의 결과를 처리하므로 모두 의존
    implementation(project(":message-core:partner-domain"))
    implementation(project(":message-core:sms-domain"))
    implementation(project(":message-core:kakao-domain"))
    implementation(project(":message-core:rcs-domain"))
    implementation(project(":message-core:naver-domain"))

    // Infrastructure
    implementation(project(":message-infrastructure:rabbitmq"))
    implementation(project(":message-infrastructure:db"))
    implementation(project(":message-infrastructure:webclient"))

    // Library
    implementation(project(":message-library:logging"))

    // Resilience4j
    implementation("io.github.resilience4j:resilience4j-retry")

    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Kotlin
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")

    // Jackson
    implementation("tools.jackson.core:jackson-databind:3.0.0")
    implementation("tools.jackson.module:jackson-module-kotlin:3.0.0")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.bootJar {
    archiveBaseName.set("reporter")
}
