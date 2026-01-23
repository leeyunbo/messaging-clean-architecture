plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
}

dependencies {
    // Netty
    implementation("io.netty:netty-all:4.1.100.Final")

    // Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")

    // Spring Context
    implementation("org.springframework:spring-context")
}
