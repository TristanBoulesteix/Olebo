val ktorVersion: String by project.parent!!
val logbackVersion: String by project.parent!!

plugins {
    application
    kotlin("multiplatform")
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

kotlin {
    sourceSets["jvmMain"].dependencies {
        implementation("io.ktor:ktor-server-core:$ktorVersion")
        implementation("io.ktor:ktor-serialization:$ktorVersion")
        implementation("io.ktor:ktor-server-netty:$ktorVersion")
        implementation("io.ktor:ktor-websockets:$ktorVersion")
        implementation("io.ktor:ktor-http-jvm:$ktorVersion")
        implementation("ch.qos.logback:logback-classic:$logbackVersion")
        implementation(project(":Update"))
        implementation(project(":System"))
    }
}