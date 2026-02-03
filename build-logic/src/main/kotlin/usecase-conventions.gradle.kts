plugins {
    id("kotlin-conventions")
    id("org.jetbrains.kotlin.plugin.spring")
    id("info.solidsoft.pitest")
}

pitest {
    junit5PluginVersion.set("1.2.1")
    pitestVersion.set("1.17.3")
    targetClasses.set(listOf("com.messaging.*"))
    threads.set(Runtime.getRuntime().availableProcessors())
    outputFormats.set(listOf("HTML", "XML"))
    mutationThreshold.set(0) // 점진적으로 올릴 예정
    timestampedReports.set(false)
}

dependencies {
    implementation("org.springframework:spring-context")
    implementation("io.github.resilience4j:resilience4j-retry")
    implementation("io.github.resilience4j:resilience4j-kotlin")
}
