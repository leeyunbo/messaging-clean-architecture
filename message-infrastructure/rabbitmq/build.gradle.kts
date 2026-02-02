plugins {
    id("infrastructure-conventions")
}

dependencies {
    implementation(project(":message-core:report-domain"))
    implementation(project(":message-core:rcs-domain"))

    api("org.springframework.boot:spring-boot-starter-amqp")
    implementation("tools.jackson.core:jackson-databind")
    implementation("tools.jackson.module:jackson-module-kotlin")
}
