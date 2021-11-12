import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinVersion: String by System.getProperties()
val serialisationVersion: String by project

plugins {
    val kotlinVersion: String by System.getProperties()
    kotlin("multiplatform") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion apply false
}

repositories {
    mavenCentral()
}

allprojects {
    group = "jdr.exia"

    apply(plugin = "org.jetbrains.kotlin.multiplatform")

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

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "16"
        kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
    }

    kotlin {
        sourceSets {
            val commonMain by getting {
                dependencies {
                    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$serialisationVersion")
                }
            }
            val jvmMain by getting
        }
    }
}

tasks.register("buildOlebo") {
    dependsOn(":Olebo:build")
}