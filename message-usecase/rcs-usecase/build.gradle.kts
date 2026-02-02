plugins {
    id("usecase-conventions")
}

dependencies {
    implementation(project(":message-core:rcs-domain"))
    implementation(project(":message-core:partner-domain"))
    implementation(project(":message-core:report-domain"))
    implementation(project(":message-library:id-generator"))
    implementation(project(":message-library:logging"))
}
