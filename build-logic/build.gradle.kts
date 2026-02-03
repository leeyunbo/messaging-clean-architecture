plugins {
    `kotlin-dsl`
}

group = "com.messaging.build-logic"

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.3.0")
    implementation("org.jetbrains.kotlin:kotlin-allopen:2.3.0")
    implementation("org.springframework.boot:spring-boot-gradle-plugin:4.0.1")
    implementation("io.spring.gradle:dependency-management-plugin:1.1.7")
    implementation("info.solidsoft.gradle.pitest:gradle-pitest-plugin:1.19.0-rc.3")
}
