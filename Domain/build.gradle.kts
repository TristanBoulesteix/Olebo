plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.composeCompiler)
}

group = "fr.olebo.domain"

kotlin {
    jvmToolchain(21)

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

    jvm()

    sourceSets {
        commonMain.dependencies {
            dependencies {
                // External dependencies
                api(libs.kodein)
                implementation(libs.kotlinx.serialization)
                implementation(libs.kotlinx.serialization.json)
                implementation(compose.ui)
                implementation(compose.runtime)
                implementation(libs.kotlinx.coroutines)
                implementation(libs.compose.viewModel)
            }
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        jvmMain.dependencies {
            implementation(libs.kotlin.reflect)
            implementation(libs.exposed.core)
        }
    }
}

