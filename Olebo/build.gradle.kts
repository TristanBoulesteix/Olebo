import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinVersion: String by System.getProperties()

plugins {
    application
    java
    kotlin("jvm")
    id("org.sonarqube") version "3.0"
}

version = "1.8.0-BETA"

repositories {
    mavenCentral()
    maven("https://dl.bintray.com/kotlin/exposed/")
    jcenter()
}

val exposedVersion = "0.29.1"

dependencies {
    testImplementation("junit", "junit", "4.12")
    implementation("org.xerial", "sqlite-jdbc", "3.28.0")
    implementation(project(":CommonModule", "default"))
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.slf4j", "slf4j-api", "1.7.25")
    implementation("org.slf4j", "slf4j-simple", "1.7.25")
    implementation("org.apache.httpcomponents", "httpclient", "4.5.10")
    implementation("org.jetbrains.kotlin", "kotlin-reflect", kotlinVersion)
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.4.2")
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-swing", "1.4.2")
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
    dependsOn(":OleboUpdater:build")
}

val run by tasks.getting(JavaExec::class) {
    dependsOn(":OleboUpdater:build")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
}

application {
    mainClassName = main
}