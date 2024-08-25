plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.mock)
}

group = "fr.olebo.viewmodel"

kotlin {
    jvmToolchain(21)

    jvm()

    sourceSets {
        jvmMain.dependencies {
            dependencies {
                // Internal dependencies
                implementation(projects.domain)

                // External dependencies
                implementation(compose.runtime)
            }
        }

        jvmTest.dependencies {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}