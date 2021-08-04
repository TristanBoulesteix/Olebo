
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

typealias JavaPath = java.nio.file.Path

val kotlinVersion: String by System.getProperties()
val coroutineVersion: String by System.getProperties()

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose") version "1.0.0-alpha1-rc4"
}

version = "0.1.0"

repositories {
    mavenCentral()
    maven("https://dl.bintray.com/kotlin/exposed/")
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

val exposedVersion = "0.32.1"

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    implementation("org.slf4j", "slf4j-simple", "2.0.0-alpha1")
    testImplementation("junit", "junit", "4.12")
    implementation("org.xerial", "sqlite-jdbc", "3.28.0")
    implementation(project(":Localization", "default"))
    implementation(project(":Update", "default"))
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", coroutineVersion)
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-swing", coroutineVersion)
    implementation(compose.desktop.currentOs)
}

val main = "jdr.exia.OleboKt"

compose.desktop {
    application {
        javaHome = System.getenv("JDK_16")

        mainClass = main

        nativeDistributions {
            packageVersion = version.toString()

            targetFormats(/*TargetFormat.Dmg,*/ TargetFormat.Msi, TargetFormat.Deb)

            modules("java.naming", "java.sql")
        }
    }
}

buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        val ktorVersion = "1.6.1"

        classpath("io.ktor:ktor-client-core:$ktorVersion")
        classpath("io.ktor:ktor-client-cio:$ktorVersion")
        classpath("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.5.1")
    }
}