import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion: String by System.getProperties()
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion apply false
}

repositories {
    mavenCentral()
}

allprojects {
    group = "jdr.exia"
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")

    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.5.10")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "16"
        kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
    }
}

tasks.register("buildOlebo") {
    dependsOn(":Olebo:build")
}