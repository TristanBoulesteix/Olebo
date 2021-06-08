
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinVersion: String by System.getProperties()

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose") version "0.4.0"
}

version = "0.1.0"

repositories {
    mavenCentral()
    maven("https://dl.bintray.com/kotlin/exposed/")
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

val exposedVersion = "0.32.1"

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.5.10")
    implementation("org.slf4j", "slf4j-simple", "2.0.0-alpha1")
    testImplementation("junit", "junit", "4.12")
    implementation("org.xerial", "sqlite-jdbc", "3.28.0")
    implementation(project(":Localization", "default"))
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.apache.httpcomponents", "httpclient", "4.5.10")
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.4.2")
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-swing", "1.4.2")
    implementation(compose.desktop.currentOs)
}

val main = "jdr.exia.OleboKt"

val jar by tasks.getting(Jar::class) {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    manifest {
        attributes["Main-Class"] = main
    }
    from(configurations.compileClasspath.map { configuration ->
        configuration.asFileTree.fold(files().asFileTree) { collection, file ->
            if (file.isDirectory) collection else collection.plus(zipTree(file))
        }
    })
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
    kotlinOptions.freeCompilerArgs += "-Xinline-classes"
}

compose.desktop {
    application {
        javaHome = System.getenv("JDK_16")

        mainClass = main

        nativeDistributions {
            packageVersion = getVersion().toString()

            targetFormats(/*TargetFormat.Dmg,*/ TargetFormat.Msi, TargetFormat.Deb)

            modules("java.naming", "java.sql")
        }
    }
}