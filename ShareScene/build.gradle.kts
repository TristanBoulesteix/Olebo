@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose") version libs.versions.compose.get()
}

group = "fr.olebo"

kotlin {
    sourceSets["commonMain"].dependencies {
        implementation(libs.bundles.ktor.client.lib)
        implementation(project(":Utils"))
        compileOnly(compose.runtime)
    }
}