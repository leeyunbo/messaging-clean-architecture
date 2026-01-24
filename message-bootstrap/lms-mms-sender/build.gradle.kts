plugins {
    kotlin("jvm")
    kotlin("plugin.spring")

}

dependencies {
    // TODO: 추후 도메인 모듈 생성 시 활성화
    // implementation(project(":message-core:sms-domain"))
    implementation(project(":message-core:partner-domain"))
    implementation(project(":message-infrastructure:rabbitmq"))
    implementation(project(":message-infrastructure:db"))
    implementation(project(":message-infrastructure:webclient"))
    implementation(project(":message-library:id-generator"))
    implementation(project(":message-library:logging"))
    implementation(project(":message-platform:skt"))
    implementation(project(":message-platform:kt"))
    implementation(project(":message-platform:lgt"))

    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("tools.jackson.core:jackson-databind:3.0.0")
    implementation("tools.jackson.module:jackson-module-kotlin:3.0.0")
}

tasks.bootJar {
    enabled = false  // 소스 코드 추가 후 활성화
}

tasks.jar {
    enabled = true
}
