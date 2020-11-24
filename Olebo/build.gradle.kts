import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.4.20"
}

group = "jdr.exia"
version = "1.4.0-BETA"

repositories {
    mavenCentral()
    maven("https://dl.bintray.com/kotlin/exposed/")
    jcenter()
}

val exposedVersion = "0.25.1"

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    testImplementation("junit", "junit", "4.12")
    implementation("org.xerial", "sqlite-jdbc", "3.28.0")
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.slf4j", "slf4j-api", "1.7.25")
    implementation("org.slf4j", "slf4j-simple", "1.7.25")
    implementation("org.apache.httpcomponents", "httpclient", "4.5.10")
    implementation("org.json", "json", "20190722")
    implementation("org.jetbrains.kotlin", "kotlin-reflect", "1.4.0")
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.4.1")
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-swing", "1.4.1")
}

val jar by tasks.getting(Jar::class) {
    manifest {
        attributes["Main-Class"] = "OleboKt"
    }
    from(configurations.compileClasspath.map { configuration ->
        configuration.asFileTree.fold(files().asFileTree) { collection, file ->
            if (file.isDirectory) collection else collection.plus(zipTree(file))
        }
    })
}

tasks.named<Wrapper>("wrapper") {
    distributionType = Wrapper.DistributionType.ALL
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}