plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.jetbrainsCompose)
}

group = "fr.olebo.domain"

kotlin {
    jvm {
        compilations.all {
            jvmToolchain(21)
        }
    }

    js(IR) {
        binaries.executable()
        browser {
            commonWebpackConfig {
                cssSupport {
                    enabled.set(true)
                }
            }
        }
    }

    sourceSets {
        commonMain.dependencies {
            dependencies {
                // External dependencies
                implementation(libs.kodein)
                implementation(libs.kotlinx.serialization)
                implementation(libs.kotlinx.serialization.json)
                implementation(compose.ui)
                implementation(compose.runtime)
            }
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        jvmMain.dependencies {
            implementation(libs.kotlin.reflect)
        }
    }
}