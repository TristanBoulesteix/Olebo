val coroutineVersion: String by project.parent!!

plugins {
    kotlin("jvm")
}

group = "jdr.exia"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":System", "default"))
    implementation("org.apache.httpcomponents", "httpclient", "4.5.10")
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", coroutineVersion)
}
