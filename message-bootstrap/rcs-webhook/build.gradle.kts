plugins {
    id("bootstrap-conventions")
}

dependencies {
    implementation(project(":message-core:rcs-domain"))
    implementation(project(":message-core:report-domain"))
    implementation(project(":message-usecase:rcs-usecase"))
    implementation(project(":message-infrastructure:rabbitmq"))
    implementation(project(":message-platform:rcs-platform"))
    implementation(project(":message-library:webhook"))
    implementation(project(":message-library:logging"))
}

tasks.bootJar {
    archiveBaseName.set("rcs-webhook")
}
