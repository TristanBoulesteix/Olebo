import kotlinx.kover.gradle.plugin.KoverGradlePlugin

plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.jetbrainsCompose) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.mock) apply false
    alias(libs.plugins.kover) apply false
}

subprojects {
    apply<KoverGradlePlugin>()
}