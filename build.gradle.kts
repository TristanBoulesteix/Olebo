plugins {
    kotlin("jvm") version "1.4.20" apply false
}

repositories {
    mavenCentral()
}

tasks.register("runOlebo") {
    dependsOn(":Olebo:run")
}

tasks.register("buildOlebo") {
    dependsOn(":Olebo:build")
}