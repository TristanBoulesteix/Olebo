val ktorVersion: String by project.parent!!
val logbackVersion: String by project.parent!!

plugins {
    application
    kotlin("jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
}

group = "fr.olebo"
version = "0.0.1"

application {
    mainClass.set("fr.olebo.ApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-serialization:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation(project(":Update"))
    implementation(project(":System", "default"))
    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")
}