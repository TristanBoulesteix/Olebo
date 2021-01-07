plugins {
    kotlin("jvm") version "1.4.20"
    kotlin("plugin.serialization") version "1.4.10" apply false
}

repositories {
    mavenCentral()
    jcenter()
}

allprojects {
    group = "jdr.exia"
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")

    dependencies {
        implementation(kotlin("stdlib-jdk8"))
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")
    }
}

tasks.register("runOlebo") {
    dependsOn(":Olebo:run")
}

tasks.register("buildOlebo") {
    dependsOn(":Olebo:build")
}