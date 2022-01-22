plugins {
    kotlin("multiplatform")

    val composeVersion: String by System.getProperties()
    id("org.jetbrains.compose") version composeVersion
}

kotlin {
    sourceSets["commonMain"].dependencies {
        compileOnly(compose.runtime)
    }
}