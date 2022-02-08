import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinVersion: String by System.getProperties()
val serialisationVersion: String by project

plugins {
    val kotlinVersion: String by System.getProperties()
    kotlin("multiplatform") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion apply false
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
                (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of(16))
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
            val commonMain by getting {
                dependencies {
                    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serialisationVersion")
                }
            }
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