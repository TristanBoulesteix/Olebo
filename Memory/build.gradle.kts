import dev.mokkery.MockMode

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.mock)
}

group = "fr.olebo.memory"

kotlin {
    jvmToolchain(21)

    jvm()

    sourceSets {
        jvmMain.dependencies {
            dependencies {
                // Internal dependencies
                implementation(projects.domain)

                // External dependencies
                implementation(libs.kotlinx.serialization)
                implementation(libs.kotlinx.serialization.json)
            }
        }

        jvmTest.dependencies {
            dependencies {
                implementation(libs.kotlin.test)

                // Internal dependencies
                implementation(projects.testsUtils)
            }
        }
    }
}

mokkery {
    defaultMockMode.set(MockMode.autoUnit)
}