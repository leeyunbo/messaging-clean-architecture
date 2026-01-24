plugins {
    kotlin("jvm")
    kotlin("plugin.spring")

}

dependencies {
    // Core
    implementation(project(":message-core:sms-domain"))
    implementation(project(":message-core:partner-domain"))

    // Infrastructure
    implementation(project(":message-infrastructure:rabbitmq"))
    implementation(project(":message-infrastructure:db"))
    implementation(project(":message-infrastructure:webclient"))

    // Library
    implementation(project(":message-library:id-generator"))
    implementation(project(":message-library:logging"))

    // Platform (SMS용 통신사)
    implementation(project(":message-platform:skt"))
    implementation(project(":message-platform:kt"))
    implementation(project(":message-platform:lgt"))

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
    archiveBaseName.set("sms-sender")
}
