import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.3.50"
}

group = "exia.jdr"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://dl.bintray.com/kotlin/exposed/")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    testCompile("junit", "junit", "4.12")
    compile("org.xerial", "sqlite-jdbc",  "3.28.0")
    compile("org.jetbrains.exposed", "exposed", "0.17.5")
    compile("org.slf4j", "slf4j-api", "1.7.25")
    compile("org.slf4j","slf4j-simple", "1.7.25")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}