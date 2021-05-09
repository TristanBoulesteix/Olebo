import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinVersion: String by System.getProperties()

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose") version "0.3.2"
}

version = "1.9.0-BETA"

repositories {
    mavenCentral()
    maven("https://dl.bintray.com/kotlin/exposed/")
    jcenter()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

val exposedVersion = "0.31.1"

dependencies {
    testImplementation("junit", "junit", "4.12")
    implementation("org.xerial", "sqlite-jdbc", "3.28.0")
    implementation(project(":CommonModule", "default"))
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.slf4j", "slf4j-api", "1.7.30")
    implementation("org.slf4j", "slf4j-simple", "1.7.30")
    implementation("org.apache.httpcomponents", "httpclient", "4.5.10")
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.4.2")
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-swing", "1.4.2")
    implementation(compose.desktop.currentOs)
}

val main = "jdr.exia.OleboKt"

val jar by tasks.getting(Jar::class) {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    manifest {
        attributes["Main-Class"] = main
    }
    from(configurations.compileClasspath.map { configuration ->
        configuration.asFileTree.fold(files().asFileTree) { collection, file ->
            if (file.isDirectory) collection else collection.plus(zipTree(file))
        }
    })
    dependsOn(":OleboUpdater:build")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
    kotlinOptions.freeCompilerArgs += "-Xinline-classes"
}

compose.desktop {
    application {
        javaHome = System.getenv("JDK_16")

        mainClass = main

        nativeDistributions {
            packageVersion = "1.9.0"

            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)

            modules("java.sql")
        }
    }
}

/*val run by tasks.getting(JavaExec::class) {
    dependsOn(":OleboUpdater:build")
}*/
