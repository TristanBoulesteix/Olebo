val coroutineVersion: String by System.getProperties()

plugins {
    kotlin("jvm")
}

group = "jdr.exia"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    //implementation(project(":System", "default"))
    implementation("org.apache.httpcomponents", "httpclient", "4.5.10")
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", coroutineVersion)
}
