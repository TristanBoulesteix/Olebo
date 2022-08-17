import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin.multiplatform)
    kotlin("plugin.serialization") version libs.versions.kotlin.get() apply false
}

allprojects {
    group = "jdr.exia"

    apply(plugin = "org.jetbrains.kotlin.multiplatform")

    repositories {
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven("https://jitpack.io")
    }

    kotlin {
        jvm {
            jvmToolchain {
                this.languageVersion.set(JavaLanguageVersion.of(16))
            }
        }
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")

    kotlin {
        js(IR) {
            binaries.executable()
            browser {
                commonWebpackConfig {
                    cssSupport.enabled = true
                }
            }
        }
        sourceSets {
            val jvmMain by getting {
                tasks.withType<KotlinCompile> {
                    kotlinOptions.jvmTarget = "16"
                }
            }
            val jsMain by getting

            all {
                languageSettings.optIn("kotlin.RequiresOptIn")
            }
        }
    }
}

tasks.register("buildOlebo") {
    dependsOn(":Olebo:build")
}