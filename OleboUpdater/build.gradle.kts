import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    java
    kotlin("jvm")
}

group = "jdr.exia"
version = "1.1.0"

repositories {
    mavenCentral()
    maven("https://dl.bintray.com/kotlin/exposed/")
    jcenter()
}

dependencies {
    implementation("org.apache.httpcomponents", "httpclient", "4.5.10")
    implementation("org.json","json", "20190722")
}

val main = "OleboUpdaterKt"

val jar by tasks.getting(Jar::class) {
    manifest {
        attributes["Main-Class"] = main
    }
    from(configurations.compileClasspath.map { configuration ->
        configuration.asFileTree.fold(files().asFileTree) { collection, file ->
            if (file.isDirectory) collection else collection.plus(zipTree(file))
        }
    })
    doLast {
       archiveFile.get().asFile.copyTo(project(":Olebo").file("src/main/resources/updater/OleboUpdater.jar"), true)
    }
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClassName = main
}