@file:Suppress("SpellCheckingInspection")

/*
* This file was generated by the Gradle 'init' task.
*
* The settings file is used to specify which projects to include in your build.
*
* Detailed information about configuring a multi-project build in Gradle can be found
* in the user manual at https://docs.gradle.org/6.3/userguide/multi_project_builds.html
*/

pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
}

rootProject.name = "Olebo"
include("Olebo")

// Dependencies
include("Localization", "Update")
include("System")
include("Website")
include("ShareScene")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            val kotlinVersion = version("kotlin", "1.7.10")
            val exposedVersion = version("exposed", "0.39.2")
            val coroutinesVersion = version("coroutines", "1.6.4")
            val ktorVersion = version("ktor", "2.1.2")
            val serialisationVersion = version("serialisation", "1.3.3")
            version("compose", "1.2.0-beta02")
            val kmdcVersion = version("kmdc", "0.0.5")

            // Plugins
            plugin("kotlin-multiplatform", "org.jetbrains.kotlin.multiplatform").versionRef(kotlinVersion)

            // kotlin-reflect
            library("kotlin-reflect", "org.jetbrains.kotlin", "kotlin-reflect").versionRef(kotlinVersion)

            // Exposed
            library("exposed-core", "org.jetbrains.exposed", "exposed-core").versionRef(exposedVersion)
            library("exposed-dao", "org.jetbrains.exposed", "exposed-dao").versionRef(exposedVersion)
            library("exposed-jdbc", "org.jetbrains.exposed", "exposed-jdbc").versionRef(exposedVersion)
            bundle("exposed", listOf("exposed-core", "exposed-dao", "exposed-jdbc"))

            // coroutines
            library("coroutines-core-jvm", "org.jetbrains.kotlinx", "kotlinx-coroutines-core-jvm").versionRef(
                coroutinesVersion
            )
            library("coroutines-swing", "org.jetbrains.kotlinx", "kotlinx-coroutines-swing").versionRef(
                coroutinesVersion
            )
            bundle("coroutines-desktop", listOf("coroutines-core-jvm", "coroutines-swing"))

            library("ktor-serialization", "io.ktor", "ktor-serialization-kotlinx-json").versionRef(ktorVersion)

            // ktor-client
            library("ktor-client-core", "io.ktor", "ktor-client-core").versionRef(ktorVersion)
            library("ktor-client-js", "io.ktor", "ktor-client-js").versionRef(ktorVersion)
            library("ktor-client-cio", "io.ktor", "ktor-client-cio").versionRef(ktorVersion)
            library("ktor-client-websockets", "io.ktor", "ktor-client-websockets").versionRef(ktorVersion)
            library("ktor-client-content-negotiation", "io.ktor", "ktor-client-content-negotiation").versionRef(
                ktorVersion
            )
            bundle(
                "ktor-client-desktop",
                listOf(
                    "ktor-client-core",
                    "ktor-client-cio",
                    "ktor-serialization",
                    "ktor-client-websockets",
                    "ktor-client-content-negotiation"
                )
            )
            bundle(
                "ktor-client-lib",
                listOf(
                    "ktor-client-core",
                    "ktor-client-websockets",
                    "ktor-serialization"
                )
            )
            bundle(
                "ktor-client-js",
                listOf(
                    "ktor-client-js",
                    "ktor-client-core",
                    "ktor-client-websockets",
                    "ktor-serialization",
                    "ktor-client-content-negotiation"
                )
            )

            // ktor-server
            library("ktor-server-core", "io.ktor", "ktor-server-core").versionRef(ktorVersion)
            library("ktor-server-redirect", "io.ktor", "ktor-server-http-redirect").versionRef(ktorVersion)
            library("ktor-server-logging", "io.ktor", "ktor-server-call-logging").versionRef(ktorVersion)
            library("ktor-server-content-negotiation", "io.ktor", "ktor-server-content-negotiation").versionRef(
                ktorVersion
            )
            library("ktor-server-netty", "io.ktor", "ktor-server-netty").versionRef(ktorVersion)
            library("ktor-server-websockets", "io.ktor", "ktor-server-websockets").versionRef(ktorVersion)
            library("ktor-server-http", "io.ktor", "ktor-http-jvm").versionRef(ktorVersion)
            library("ktor-server-html", "io.ktor", "ktor-server-html-builder").versionRef(ktorVersion)
            bundle(
                "ktor-server",
                listOf(
                    "ktor-server-core",
                    "ktor-server-redirect",
                    "ktor-server-logging",
                    "ktor-server-content-negotiation",
                    "ktor-server-netty",
                    "ktor-server-websockets",
                    "ktor-server-http",
                    "ktor-server-html",
                    "ktor-serialization"
                )
            )

            // serialization
            library("serialization", "org.jetbrains.kotlinx", "kotlinx-serialization-core").versionRef(
                serialisationVersion
            )
            library("serialization-jvm", "org.jetbrains.kotlinx", "kotlinx-serialization-core-jvm").versionRef(
                serialisationVersion
            )
            library("serialization-json", "org.jetbrains.kotlinx", "kotlinx-serialization-json").versionRef(
                serialisationVersion
            )

            // kmdc
            library("kmdc-button", "dev.petuska", "kmdc-button").versionRef(kmdcVersion)
            library("kmdc-textfield", "dev.petuska", "kmdc-textfield").versionRef(kmdcVersion)
            bundle("kmdc", listOf("kmdc-button", "kmdc-textfield"))

            // Other
            library("slf4j", "org.slf4j", "slf4j-simple").version("2.0.0-alpha7")
            library("jdbc-sqlite", "org.xerial", "sqlite-jdbc").version("3.36.0.3")
            library("systemThemeDetector", "com.github.Dansoftowner", "jSystemThemeDetector").version("3.8")
            library("logback", "ch.qos.logback", "logback-classic").version("1.2.3")
        }

        create("npm") {
            // npm versions
            version("style-loader", "^3.3.0")
            version("css-loader", "^6.3.0")
            version("sass-loader", "^13.0.0")
            version("sass", "^1.52.1")
        }
    }
}
