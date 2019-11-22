import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.concurrent.Callable

plugins {
    java
    kotlin("jvm") version "1.3.50"
}

group = "jdr.exia"
version = "1.0-BETA"

repositories {
    mavenCentral()
    maven("https://dl.bintray.com/kotlin/exposed/")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    testCompile("junit", "junit", "4.12")
    compile("org.xerial", "sqlite-jdbc",  "3.28.0")
    compile("org.jetbrains.exposed", "exposed", "0.17.7")
    compile("org.slf4j", "slf4j-api", "1.7.25")
    compile("org.slf4j","slf4j-simple", "1.7.25")
    compile("org.apache.httpcomponents", "httpclient", "4.5.10")
    compile("org.json","json", "20190722")
}

val jar by tasks.getting(Jar::class) {
    manifest {
        attributes["Main-Class"] = "jdr.exia.OleboKt"
    }
    from(configurations.compile.map { configuration ->
        configuration.asFileTree.fold(files().asFileTree) { collection, file ->
            if (file.isDirectory) collection else collection.plus(zipTree(file))
        }
    })
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}