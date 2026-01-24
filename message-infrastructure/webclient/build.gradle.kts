plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
}

dependencies {
    // Infrastructure는 Core에 의존하지 않음 (순수 기술 모듈)

    // Spring WebFlux (WebClient)
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    // Resilience4j Circuit Breaker
    implementation("io.github.resilience4j:resilience4j-spring-boot3:2.2.0")
    implementation("io.github.resilience4j:resilience4j-reactor:2.2.0")

    // Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
}
