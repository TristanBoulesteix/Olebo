import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.3.50"
}

group = "jdr.exia.updater"

repositories {
    mavenCentral()
    maven("https://dl.bintray.com/kotlin/exposed/")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    compile("org.jetbrains.exposed", "exposed", "0.17.7")
    compile("org.apache.httpcomponents", "httpclient", "4.5.10")
    compile("org.json","json", "20190722")
}

val jar by tasks.getting(Jar::class) {
    manifest {
        attributes["Main-Class"] = "jdr.exia.updater.OleboUpdaterKt"
    }
    from(configurations.compile.map { configuration ->
        configuration.asFileTree.fold(files().asFileTree) { collection, file ->
            if (file.isDirectory) collection else collection.plus(zipTree(file))
        }
    })
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}