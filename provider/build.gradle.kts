plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    jacoco
}

jacoco {
    toolVersion = "0.8.12"
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required = true
        html.required = true
    }
}

dependencies {
    implementation(project(":common"))

    // WebFlux (WebClient for provider 호출)
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    // Resilience4j (Circuit Breaker, Timeout, Retry)
    implementation("io.github.resilience4j:resilience4j-spring-boot3:2.2.0")
    implementation("io.github.resilience4j:resilience4j-reactor:2.2.0")

    // MockWebServer (테스트용 Mock 서버)
    testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")

    // Jackson 3 Kotlin Module
    testImplementation("tools.jackson.module:jackson-module-kotlin:3.0.3")

    // JUnit Platform Launcher (테스트 실행에 필요)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
