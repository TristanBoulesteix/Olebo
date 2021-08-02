@file:Suppress("PropertyName")

val ktor_version: String by project
val logback_version: String by project

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
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-serialization:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation(project(":Update"))
    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
}