import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    jvmToolchain(21)

    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(compose.components.resources)
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

compose.desktop {
    application {
        mainClass = "fr.olebo.Olebo"

        System.getenv("JDK_21")?.let {
            javaHome = it
        }

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