plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
}

dependencies {
    implementation(project(":message-core:naver-domain"))
    implementation(project(":message-library:logging"))

    // Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")

    // Spring Context
    implementation("org.springframework:spring-context")

    // Resilience4j Retry
    implementation("io.github.resilience4j:resilience4j-retry")
    implementation("io.github.resilience4j:resilience4j-kotlin")
}
