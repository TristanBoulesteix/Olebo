import org.jetbrains.compose.desktop.application.dsl.TargetFormat

val kotlinVersion: String by System.getProperties()
val coroutineVersion: String by project.parent!!
val ktorVersion: String by project.parent!!
val exposedVersion: String by project.parent!!

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose") version "1.0.0-beta6-dev474"
    id("org.sonarqube") version "3.3"
}

version = "0.1.4"

repositories {
    mavenCentral()
    maven("https://dl.bintray.com/kotlin/exposed/")
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

kotlin {
    sourceSets["jvmMain"].dependencies {
        implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
        implementation("org.slf4j:slf4j-simple:2.0.0-alpha1")
        implementation("org.xerial:sqlite-jdbc:3.28.0")
        implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
        implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
        implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutineVersion")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:$coroutineVersion")
        implementation("io.ktor:ktor-client-core:$ktorVersion")
        implementation("io.ktor:ktor-client-cio:$ktorVersion")
        implementation("io.ktor:ktor-client-serialization:$ktorVersion")
        implementation("io.ktor:ktor-client-apache:$ktorVersion")
        implementation(project(":Localization"))
        implementation(project(":Update"))
        implementation(project(":System"))
        implementation(compose.desktop.currentOs)
    }
}

val main = "jdr.exia.OleboKt"

compose.desktop {
    application {
        javaHome = System.getenv("JDK_16")

        mainClass = main

        nativeDistributions {
            packageName = "Olebo"
            packageVersion = version.toString()

            targetFormats(/*TargetFormat.Dmg,*/ TargetFormat.Msi, TargetFormat.Deb)

            modules("java.naming", "java.sql", "jdk.localedata")
        }
    }
}