plugins {
    kotlin("jvm")
    kotlin("plugin.spring")

}

dependencies {
    // Core
    implementation(project(":message-core:kakao-domain"))
    implementation(project(":message-core:partner-domain"))

    // UseCase
    implementation(project(":message-usecase:kakao-direct"))

    // Infrastructure
    implementation(project(":message-infrastructure:rabbitmq"))
    implementation(project(":message-infrastructure:db"))
    implementation(project(":message-infrastructure:webclient"))

    // Library
    implementation(project(":message-library:id-generator"))
    implementation(project(":message-library:logging"))

    // Platform
    implementation(project(":message-platform:kakao-direct"))

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
    enabled = false  // 소스 코드 추가 후 활성화
}

tasks.jar {
    enabled = true
}
