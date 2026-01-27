plugins {
    id("usecase-conventions")
}

dependencies {
    implementation(project(":message-core:naver-domain"))
    implementation(project(":message-core:report-domain"))
    implementation(project(":message-library:logging"))
}
