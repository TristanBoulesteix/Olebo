import Build_gradle.JavaPath
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import java.io.BufferedOutputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.swing.JOptionPane
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.div

typealias JavaPath = java.nio.file.Path

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

buildscript {
    repositories {
        mavenCentral()
    }

    dependencies {
        val ktorVersion = "1.6.1"

        classpath("io.ktor:ktor-client-core:$ktorVersion")
        classpath("io.ktor:ktor-client-cio:$ktorVersion")
        classpath("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.5.1")
    }
}

tasks.register<PublishToServer>("publish") {
    dependsOn("createDistributable")
    buildPath.set(buildDir.toPath())
    versionName.set(version.toString())
}

abstract class PublishToServer : DefaultTask() {
    @get:Input
    abstract val buildPath: Property<JavaPath>

    @get:Input
    abstract val versionName: Property<String>

    @ExperimentalPathApi
    @TaskAction
    fun action() {
        val exePath = buildPath.get() / "compose" / "binaries" / "main" / "app" / "Olebo"

        val versionCode = getVersionCode() ?: run {
            System.err.println("Publication de version annulée")
            return
        }

        val versionName = getVersionName() ?: run {
            System.err.println("Publication de version annulée")
            return
        }

        HttpClient(CIO).use {
            runBlocking {
                try {
                    val response = it.submitFormWithBinaryData<HttpResponse>(
                        url = "https://olebo.fr/versions",
                        formData = formData {
                            append("zip", zipDirectory(exePath.toFile()))
                            append("code", versionCode)
                            append("name", versionName)
                        }) {
                        this.onUpload { bytesSentTotal, contentLength ->
                            println("Sent $bytesSentTotal bytes from $contentLength")
                        }
                    }
                    println(response.status)
                } catch (e: Throwable) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun getVersionCode(): Int? {
        val versionCode = JOptionPane.showInputDialog("Entrez le code de version") ?: return null

        return versionCode.toIntOrNull() ?: getVersionCode()
    }

    private fun getVersionName(): String? {
        val version = versionName.get()

        val confirm = JOptionPane.showConfirmDialog(
            null,
            "Confimez le nom de la version : $version",
            "Publication de Olebo v. $version",
            JOptionPane.YES_NO_OPTION
        )

        return version.takeIf { confirm == JOptionPane.OK_OPTION }
    }

    private fun createTempFile() = File.createTempFile("Olebo_build", ".olebo.build").also {
        it.deleteOnExit()
    }

    private fun zipDirectory(directoryToZip: File): ByteArray {
        val outputTempZip = createTempFile()

        ZipOutputStream(BufferedOutputStream(FileOutputStream(outputTempZip))).use { zos ->
            directoryToZip.walkTopDown().forEach { file ->
                val zipFileName =
                    file.absolutePath.removePrefix(directoryToZip.absolutePath).removePrefix(File.separator)
                        .replace('\\', '/')

                if (zipFileName.isNotBlank()) {
                    val entry = ZipEntry("$zipFileName${(if (file.isDirectory) "/" else "")}")
                    zos.putNextEntry(entry)
                    if (file.isFile) {
                        file.inputStream().use { it.copyTo(zos) }
                    }
                }
            }
        }

        return outputTempZip.readBytes()
    }
}