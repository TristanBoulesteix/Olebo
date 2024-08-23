import dev.mokkery.gradle.ApplicationRule.Listed

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.mock)
}

group = "fr.olebo.tests.utils"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(21)

    jvm()
    js {
        browser()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlin.test)

            // External dependencies
            implementation(libs.kotlinx.serialization)
        }
    }
}

mokkery {
    rule.set(Listed("commonMain"))
}