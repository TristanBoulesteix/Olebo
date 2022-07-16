import org.jetbrains.compose.desktop.application.dsl.TargetFormat

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose") version libs.versions.compose.get()
}

version = "0.1.5"

repositories {
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    maven("https://dl.bintray.com/kotlin/exposed/")
}

kotlin {
    sourceSets["jvmMain"].dependencies {
        implementation(libs.kotlin.reflect)
        implementation(libs.slf4j)
        implementation(libs.jdbc.sqlite)
        implementation(libs.bundles.exposed)
        implementation(libs.bundles.coroutines.desktop)
        implementation(libs.bundles.ktor.client.desktop)
        implementation(libs.serialization.jvm)
        implementation(libs.systemThemeDetector)
        implementation(project(":Localization"))
        implementation(project(":Update"))
        implementation(project(":System"))
        implementation(project(":ShareScene"))
        implementation(compose.desktop.currentOs)
    }
}

val main = "jdr.exia.OleboKt"

compose.desktop {
    application {
        javaHome = System.getenv("JDK_16")

        mainClass = main

        nativeDistributions {
            packageName = "Olebo"
            packageVersion = version.toString()

            targetFormats(/*TargetFormat.Dmg,*/ TargetFormat.Msi, TargetFormat.Deb)

            modules("java.naming", "java.sql", "jdk.localedata")
        }
    }
}