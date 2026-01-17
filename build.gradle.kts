import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.3.0" apply false
    kotlin("plugin.spring") version "2.3.0" apply false
    kotlin("plugin.jpa") version "2.3.0" apply false
    id("org.springframework.boot") version "4.0.1" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
}

allprojects {
    group = "com.messaging"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "kotlin")
    apply(plugin = "kotlin-spring")
    apply(plugin = "io.spring.dependency-management")

    the<io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension>().apply {
        imports {
            mavenBom("org.springframework.boot:spring-boot-dependencies:4.0.1")
        }
    }

    dependencies {
        // Kotlin
        "implementation"("org.jetbrains.kotlin:kotlin-reflect")

        // Logging
        "implementation"("io.github.microutils:kotlin-logging-jvm:3.0.5")

        // Reactor Kotlin Extensions
        "implementation"("io.projectreactor.kotlin:reactor-kotlin-extensions")

        // Coroutines (Reactor 연동)
        "implementation"("org.jetbrains.kotlinx:kotlinx-coroutines-core")
        "implementation"("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

        // Test
        "testImplementation"("org.springframework.boot:spring-boot-starter-test")
        "testImplementation"("io.projectreactor:reactor-test")
    }

    configure<org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension> {
        jvmToolchain(21)
    }

    tasks.withType<KotlinCompile> {
        compilerOptions {
            freeCompilerArgs.add("-Xjsr305=strict")
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}
