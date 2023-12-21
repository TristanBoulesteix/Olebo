@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose") version libs.versions.compose.get()
}

group = "fr.olebo.utils"

kotlin {
    sourceSets["commonMain"].dependencies {
        compileOnly(compose.runtime)
    }
}