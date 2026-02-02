plugins {
    id("bootstrap-conventions")
}

dependencies {
    implementation(project(":message-core:rcs-domain"))
    implementation(project(":message-core:partner-domain"))
    implementation(project(":message-usecase:rcs-usecase"))
    implementation(project(":message-infrastructure:rabbitmq"))
    implementation(project(":message-infrastructure:netty"))
    implementation(project(":message-library:id-generator"))
    implementation(project(":message-library:logging"))
}

tasks.bootJar {
    archiveBaseName.set("rcs-receiver")
}
