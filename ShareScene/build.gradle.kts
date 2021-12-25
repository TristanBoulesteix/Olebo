val ktorVersion: String by project.parent!!

plugins {
    kotlin("multiplatform")
}

group = "fr.olebo"

kotlin {
    sourceSets["commonMain"].dependencies {
        implementation("io.ktor:ktor-websockets:$ktorVersion")
    }
}