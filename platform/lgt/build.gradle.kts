plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
}

dependencies {
    // SMS 도메인 의존
    implementation(project(":core:sms-domain"))
    implementation(project(":infrastructure:webclient"))
    implementation(project(":library:logging"))

    // Spring WebFlux
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    // Resilience4j
    implementation("io.github.resilience4j:resilience4j-circuitbreaker:2.2.0")
    implementation("io.github.resilience4j:resilience4j-reactor:2.2.0")

    // Jackson
    implementation("tools.jackson.core:jackson-databind:3.0.0")
    implementation("tools.jackson.module:jackson-module-kotlin:3.0.0")
}
