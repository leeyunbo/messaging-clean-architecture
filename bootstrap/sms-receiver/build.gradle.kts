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

    // Library
    implementation(project(":library:id-generator"))
    implementation(project(":library:logging"))

    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")

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
    archiveBaseName.set("sms-receiver")
}
