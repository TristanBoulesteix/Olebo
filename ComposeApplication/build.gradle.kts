import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
}

kotlin {
    jvm {
        compilations.all {
            jvmToolchain(21)
        }
    }

    sourceSets {
        jvmMain.dependencies {
            dependencies { // Internal dependencies
                implementation(projects.domain)
                implementation(projects.persistence)
                implementation(projects.system)

                // Compose dependencies
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.desktop.currentOs)

                // External dependencies
                implementation(libs.kodein)
                implementation(libs.kodein.compose)
            }
        }

        jvmTest.dependencies {
            dependencies { // Test dependencies
                implementation(libs.kotlin.test)
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg/*, TargetFormat.Msi*/, TargetFormat.Deb)
            packageName = "fr.olebo"
            packageVersion = "2.0.0"
        }
    }
}