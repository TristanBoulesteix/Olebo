import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm")
}

version = "1.1.0"

repositories {
    mavenCentral()
    maven("https://dl.bintray.com/kotlin/exposed/")
    jcenter()
}

dependencies {
    implementation("org.apache.httpcomponents", "httpclient", "4.5.10")
    implementation("org.json","json", "20190722")
    implementation(project(":Localization","default"))
    implementation(project(":CommonModule","default"))
}

val main = "jdr.exia.updater.OleboUpdaterKt"

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