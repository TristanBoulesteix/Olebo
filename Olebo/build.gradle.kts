import org.jetbrains.compose.desktop.application.dsl.TargetFormat

val kotlinVersion: String by System.getProperties()
val coroutineVersion: String by project.parent!!
val ktorVersion: String by project.parent!!
val exposedVersion: String by project.parent!!

plugins {
    kotlin("multiplatform")

    val composeVersion: String by System.getProperties()
    id("org.jetbrains.compose") version composeVersion
}

version = "0.1.4"

repositories {
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://dl.bintray.com/kotlin/exposed/")
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
        implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
        implementation("io.ktor:ktor-client-websockets:$ktorVersion")
        implementation("com.github.Dansoftowner:jSystemThemeDetector:3.6")
        implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
        implementation(project(":Localization"))
        implementation(project(":Update"))
        implementation(project(":System"))
        implementation(project(":ShareScene"))
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