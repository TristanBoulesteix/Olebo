plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

group = "fr.olebo.system"

kotlin {
    jvmToolchain(21)

    jvm()

    sourceSets {
        jvmMain.dependencies {
            dependencies {
                // Internal dependencies
                implementation(projects.domain)

                // External dependencies
                implementation(libs.kodein)
            }
        }

        jvmTest.dependencies {
          dependencies {
              implementation(libs.kotlin.test)
          }
        }
    }
}