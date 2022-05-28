val coroutineVersion: String by project.parent!!
val serialisationVersion: String by project.parent!!

plugins {
    kotlin("multiplatform")
}

group = "jdr.exia"

kotlin {
    sourceSets["jvmMain"].dependencies {
        api(project(":System"))
        api("org.jetbrains.kotlinx:kotlinx-serialization-json:$serialisationVersion")
        api("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutineVersion")
    }
}