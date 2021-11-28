val coroutineVersion: String by project.parent!!

plugins {
    kotlin("multiplatform")
}

group = "jdr.exia"

kotlin {
    sourceSets["jvmMain"].dependencies {
        implementation(project(":System"))
        implementation("org.apache.httpcomponents:httpclient:4.5.10")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutineVersion")
    }
}