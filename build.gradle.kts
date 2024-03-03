import kotlinx.kover.gradle.plugin.KoverGradlePlugin

plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.jetbrainsCompose) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.mock) apply false
    alias(libs.plugins.kover)
}

subprojects {
    apply<KoverGradlePlugin>()
}

dependencies {
    kover(projects.composeApplication)
    kover(projects.domain)
    kover(projects.system)
    kover(projects.persistence)
}

koverReport {
    defaults {
        xml {
            setReportFile(project.file(".qodana/code-coverage/coverage"))
        }
    }
}