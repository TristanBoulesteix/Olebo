plugins {
    alias(libs.plugins.kotlinJvm)
}

group = "fr.olebo.persistence"

kotlin {
    jvmToolchain(17)
}

dependencies {
    // Internal dependencies
    implementation(projects.domain)

    // External dependencies
    implementation(libs.koin.core)

    // Test dependencies
    testImplementation(libs.kotlin.test)
}

tasks.test {
    useJUnitPlatform()
}