plugins {
    val kotlinVersion : String by System.getProperties()
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion apply false
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

    val compileKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
    compileKotlin.kotlinOptions.useIR = true
}

tasks.register("runOlebo") {
    dependsOn(":Olebo:run")
}

tasks.register("buildOlebo") {
    dependsOn(":Olebo:build")
}