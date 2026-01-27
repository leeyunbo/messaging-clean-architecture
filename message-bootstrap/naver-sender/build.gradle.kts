plugins {
    id("bootstrap-conventions")
}

dependencies {
    implementation(project(":message-core:naver-domain"))
    implementation(project(":message-core:partner-domain"))
    implementation(project(":message-usecase:naver-usecase"))
    implementation(project(":message-infrastructure:rabbitmq"))
    implementation(project(":message-platform:naver-platform"))
    implementation(project(":message-library:id-generator"))
    implementation(project(":message-library:logging"))
}

tasks.bootJar {
    archiveBaseName.set("naver-sender")
}
