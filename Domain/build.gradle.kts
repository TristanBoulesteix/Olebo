plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

group = "fr.olebo.domain"

kotlin {
    jvm {
        compilations.all {
            jvmToolchain(17)
        }
    }

    sourceSets {
        val jvmMain by getting

        val commonMain by getting {
            dependencies {
                // External dependencies
                implementation(libs.kodein)
                implementation(libs.kotlinx.serialization)
                implementation(libs.kotlinx.serialization.json)
            }
        }
    }
}