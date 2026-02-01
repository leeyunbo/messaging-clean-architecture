plugins {
    id("platform-conventions")
}

dependencies {
    implementation(project(":message-core:rcs-domain"))
    implementation(project(":message-core:report-domain"))
    implementation(project(":message-library:logging"))
}
