plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    `java-library`  // api 키워드 사용을 위해
}

dependencies {
    // Port 인터페이스
    implementation(project(":message-core:report-domain"))

    // Spring AMQP
    api("org.springframework.boot:spring-boot-starter-amqp")

    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter")

    // Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    // Jackson for JSON
    implementation("tools.jackson.core:jackson-databind:3.0.0")
    implementation("tools.jackson.module:jackson-module-kotlin:3.0.0")
}
