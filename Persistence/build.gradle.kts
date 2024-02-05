plugins {
    alias(libs.plugins.kotlinJvm)
}

group = "fr.olebo.persistence"

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(projects.domain)
}