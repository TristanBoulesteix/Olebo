val ktorVersion: String by project.parent!!
val logbackVersion: String by project.parent!!

plugins {
    application
    kotlin("multiplatform")
    id("org.jetbrains.kotlin.plugin.serialization")

    val composeVersion: String by System.getProperties()
    id("org.jetbrains.compose") version composeVersion
}

group = "fr.olebo"
version = "0.0.1"

application {
    mainClass.set("fr.olebo.ApplicationKt")
}

kotlin {
    sourceSets["commonMain"].dependencies {
        implementation(compose.runtime)
    }
    sourceSets["jvmMain"].dependencies {
        implementation("io.ktor:ktor-server-core:$ktorVersion")
        implementation("io.ktor:ktor-serialization:$ktorVersion")
        implementation("io.ktor:ktor-server-netty:$ktorVersion")
        implementation("io.ktor:ktor-websockets:$ktorVersion")
        implementation("io.ktor:ktor-http:$ktorVersion")
        implementation("ch.qos.logback:logback-classic:$logbackVersion")
        implementation("io.ktor:ktor-html-builder:$ktorVersion")
        implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.2")
        implementation(project(":Update"))
        implementation(project(":System"))
        implementation(project(":ShareScene"))
    }
    sourceSets["jsMain"].dependencies {
        implementation(compose.web.core)
        implementation("dev.petuska:kmdc:0.0.1")
    }
}

tasks.named<Copy>("jvmProcessResources") {
    val jsBrowserDistribution = tasks.named("jsBrowserDistribution")
    from(jsBrowserDistribution)
}

tasks.named<JavaExec>("run") {
    dependsOn(tasks.named<Jar>("jvmJar"))
    classpath(tasks.named<Jar>("jvmJar"))
}