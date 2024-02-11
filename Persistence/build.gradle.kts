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
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.jdbc.sqlite)
    implementation(libs.koin.core)

    // Test dependencies
    testImplementation(libs.kotlin.test)
}

tasks.test {
    useJUnitPlatform()
}