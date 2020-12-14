plugins {
    kotlin("jvm") version "1.4.20"
}

repositories {
    mavenCentral()
}

allprojects {
    group = "jdr.exia"
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    dependencies {
        implementation(kotlin("stdlib-jdk8"))
    }
}

tasks.register("runOlebo") {
    dependsOn(":Olebo:run")
}

tasks.register("buildOlebo") {
    dependsOn(":Olebo:build")
}