import org.jetbrains.compose.desktop.application.dsl.TargetFormat

val kotlinVersion: String by System.getProperties()
val coroutineVersion: String by System.getProperties()

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose") version "0.4.0"
}

version = "0.1.0"

repositories {
    mavenCentral()
    maven("https://dl.bintray.com/kotlin/exposed/")
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

val exposedVersion = "0.32.1"

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.5.10")
    implementation("org.slf4j", "slf4j-simple", "2.0.0-alpha1")
    testImplementation("junit", "junit", "4.12")
    implementation("org.xerial", "sqlite-jdbc", "3.28.0")
    implementation(project(":Localization", "default"))
    implementation(project(":Update", "default"))
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", coroutineVersion)
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-swing", coroutineVersion)
    implementation(compose.desktop.currentOs)
}

val main = "jdr.exia.OleboKt"

/*val jar by tasks.getting(Jar::class) {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    manifest {
        attributes["Main-Class"] = main
    }
    from(configurations.compileClasspath.map { configuration ->
        configuration.asFileTree.fold(files().asFileTree) { collection, file ->
            if (file.isDirectory) collection else collection.plus(zipTree(file))
        }
    })
}*/

compose.desktop {
    application {
        javaHome = System.getenv("JDK_16")

        mainClass = main

        nativeDistributions {
            packageVersion = getVersion().toString()

            targetFormats(/*TargetFormat.Dmg,*/ TargetFormat.Msi, TargetFormat.Deb)

            modules("java.naming", "java.sql")
        }
    }
}

tasks.register<PublishToServer>("publish") {
    dependsOn("createDistributable")
    buildPath = buildDir.toPath()
}

abstract class PublishToServer : DefaultTask() {
    lateinit var buildPath: java.nio.file.Path

    @TaskAction
    fun action() {
        val exePath = buildPath
    }
}