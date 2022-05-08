val coroutineVersion: String by project.parent!!
val serialisationVersion: String by project.parent!!

plugins {
    kotlin("multiplatform")
}

group = "jdr.exia"

kotlin {
    sourceSets["jvmMain"].dependencies {
        implementation(project(":System"))
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serialisationVersion")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutineVersion")
    }
}