val ktorVersion: String by project.parent!!

plugins {
    kotlin("multiplatform")
}

group = "fr.olebo"

kotlin {
    sourceSets["commonMain"].dependencies {
        implementation("io.ktor:ktor-client-core:$ktorVersion")
        implementation("io.ktor:ktor-client-websockets:$ktorVersion")
    }
}