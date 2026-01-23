plugins {
    kotlin("jvm")
    kotlin("plugin.spring")

}

dependencies {
    // Core
    implementation(project(":core:kakao-domain"))
    implementation(project(":core:partner-domain"))

    // UseCase
    implementation(project(":usecase:kakao-direct"))

    // Infrastructure
    implementation(project(":infrastructure:rabbitmq"))
    implementation(project(":infrastructure:db"))
    implementation(project(":infrastructure:webclient"))

    // Library
    implementation(project(":library:id-generator"))
    implementation(project(":library:logging"))

    // Platform
    implementation(project(":platform:kakao-direct"))

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
