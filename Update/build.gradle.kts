plugins {
    kotlin("multiplatform")
}

group = "jdr.exia"

kotlin {
    sourceSets["jvmMain"].dependencies {
        implementation(project(":System"))
        implementation(libs.serialization)
        implementation(libs.coroutines.core.jvm)
    }
}