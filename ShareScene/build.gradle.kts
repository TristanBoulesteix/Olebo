val ktorVersion: String by project.parent!!
val serialisationVersion: String by project.parent!!

plugins {
    kotlin("multiplatform")

    val composeVersion: String by System.getProperties()
    id("org.jetbrains.compose") version composeVersion
}

group = "fr.olebo"

kotlin {
    sourceSets["commonMain"].dependencies {
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serialisationVersion")
        implementation("io.ktor:ktor-client-core:$ktorVersion")
        implementation("io.ktor:ktor-client-websockets:$ktorVersion")
        compileOnly(compose.runtime)
    }
}