plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.mock)
}

group = "fr.olebo.persistence"

kotlin {
    jvm {
        compilations.all {
            jvmToolchain(21)
        }
    }

    sourceSets {
        jvmMain.dependencies {
            // Internal dependencies
            implementation(projects.domain)
            implementation(projects.domain)

            // External dependencies
            implementation(libs.exposed.core)
            implementation(libs.exposed.dao)
            implementation(libs.exposed.jdbc)
            implementation(libs.jdbc.sqlite)
            implementation(libs.kodein)
            implementation(libs.slf4j)
            implementation(libs.kotlinx.coroutines)
        }

        commonTest.dependencies {
            // Test dependencies
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}