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
    implementation(libs.kodein)

    // Test dependencies
    testImplementation(libs.kotlin.test)
}

tasks.test {
    useJUnitPlatform()
}