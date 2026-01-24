plugins {
    kotlin("jvm")
    kotlin("plugin.spring")

}

dependencies {
    // Core - Consumer에서 직접 사용
    implementation(project(":message-core:kakao-domain"))

    // UseCase
    implementation(project(":message-usecase:kakao"))

    // Infrastructure
    implementation(project(":message-infrastructure:rabbitmq"))
    implementation(project(":message-infrastructure:db"))
    implementation(project(":message-infrastructure:webclient"))

    // Platform
    implementation(project(":message-platform:kakao"))

    // Library
    implementation(project(":message-library:logging"))

    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    runtimeOnly("io.r2dbc:r2dbc-h2")

    // Jackson - Consumer에서 메시지 파싱용
    implementation("tools.jackson.module:jackson-module-kotlin")

    // Jackson 2 - Resilience4j actuator 호환성용 (Spring Boot가 버전 관리)
    runtimeOnly("com.fasterxml.jackson.core:jackson-core")
    runtimeOnly("com.fasterxml.jackson.core:jackson-databind")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.bootJar {
    archiveBaseName.set("kakao-alimtalk-sender")
}
