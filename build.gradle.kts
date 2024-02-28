plugins {
    // this is necessary to avoid the plugins to be loaded multiple times
    // in each subproject's classloader
    alias(libs.plugins.jetbrainsCompose) apply false
    alias(libs.plugins.kotlinJvm) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
}

subprojects {
    afterEvaluate {
        when {
            plugins.hasPlugin("java") -> {
                tasks.register<Test>("allTests") {
                    description = "Run JVM tests"
                    group = "Tests"
                    useJUnitPlatform()
                }
            }
        }
    }
}