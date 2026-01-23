plugins {
    kotlin("jvm")
    kotlin("plugin.spring")

}

dependencies {
    // Core
    implementation(project(":core:sms-domain"))
    implementation(project(":core:partner-domain"))

    // Infrastructure
    implementation(project(":infrastructure:rabbitmq"))
    implementation(project(":infrastructure:db"))
    implementation(project(":infrastructure:webclient"))

    // Library
    implementation(project(":library:id-generator"))
    implementation(project(":library:logging"))

    // Platform (SMS용 통신사)
    implementation(project(":platform:skt"))
    implementation(project(":platform:kt"))
    implementation(project(":platform:lgt"))

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
