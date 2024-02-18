plugins {
    alias(libs.plugins.kotlinJvm)
}

group = "fr.olebo.domain"

kotlin {
    jvmToolchain(17)
}

dependencies {
    // External dependencies
    implementation(libs.kodein)
}