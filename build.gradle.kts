plugins {
    kotlin("jvm") version "1.4.20"
}

repositories {
    mavenCentral()
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    dependencies {
        implementation(kotlin("stdlib-jdk8"))
        implementation(project(":Localization","default"))
    }
}

tasks.register("runOlebo") {
    dependsOn(":Olebo:run")
}

tasks.register("buildOlebo") {
    dependsOn(":Olebo:build")
}