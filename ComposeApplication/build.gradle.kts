import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeFeatureFlag

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.compose.hotReload)
    alias(libs.plugins.mock)
}

kotlin {
    jvmToolchain(21)

    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(compose.components.resources)
            implementation(libs.compose.viewModel)
        }

        jvmMain.dependencies {
            // Internal dependencies
            implementation(projects.domain)
            implementation(projects.persistence)
            implementation(projects.system)
            implementation(projects.memory)

            // Compose dependencies
            implementation(compose.desktop.currentOs)

            // External dependencies
            implementation(libs.kodein.compose)
            implementation(libs.kotlinx.coroutines.swing)
        }

        jvmTest.dependencies {
            // Test dependencies
            implementation(libs.kotlin.test)
            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
        }
    }
}

composeCompiler {
    featureFlags.add(ComposeFeatureFlag.OptimizeNonSkippingGroups)
}

compose.desktop {
    application {
        mainClass = "fr.olebo.Olebo"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg/*, TargetFormat.Msi*/, TargetFormat.Deb)
            packageName = "fr.olebo"
            packageVersion = "2.0.0"
        }
    }
}

compose.resources {
    packageOfResClass = "fr.olebo.resources"
}