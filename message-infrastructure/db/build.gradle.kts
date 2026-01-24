plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
}

dependencies {
    // Core 도메인들
    implementation(project(":message-core:partner-domain"))
    implementation(project(":message-core:sms-domain"))
    implementation(project(":message-core:kakao-domain"))
    implementation(project(":message-core:rcs-domain"))
    implementation(project(":message-core:naver-domain"))
    implementation(project(":message-core:report-domain"))

    // Spring Data R2DBC
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.postgresql:r2dbc-postgresql:1.0.7.RELEASE")

    // Kotlin Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    // Jackson for JSON (detail 필드 처리)
    implementation("tools.jackson.core:jackson-databind:3.0.0")
    implementation("tools.jackson.module:jackson-module-kotlin:3.0.0")
}
