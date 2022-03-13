val ktorVersion: String by project.parent!!
val logbackVersion: String by project.parent!!
val kmdcVersion: String by project.parent!!

plugins {
    application
    kotlin("multiplatform")
    id("org.jetbrains.kotlin.plugin.serialization")

    val composeVersion: String by System.getProperties()
    id("org.jetbrains.compose") version composeVersion
}

group = "fr.olebo"
version = "1.0.0"

application {
    mainClass.set("fr.olebo.ApplicationKt")
}

distributions {
    main {
        contents {
            from("$buildDir/libs") {
                exclude(project.name)
                rename("${project.name}-jvm", project.name)
                into("lib")
            }
        }
    }
}

kotlin {
    jvm { withJava() } // Required to deploy app with Gradle distribution plugin
    sourceSets["commonMain"].dependencies {
        implementation(compose.runtime)
        implementation(project(":ShareScene"))
        implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    }
    sourceSets["jvmMain"].dependencies {
        implementation("io.ktor:ktor-server-core:$ktorVersion")
        implementation("io.ktor:ktor-server-http-redirect:$ktorVersion")
        implementation("io.ktor:ktor-server-call-logging:$ktorVersion")
        implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
        implementation("io.ktor:ktor-serialization:$ktorVersion")
        implementation("io.ktor:ktor-server-netty:$ktorVersion")
        implementation("io.ktor:ktor-server-websockets:$ktorVersion")
        implementation("io.ktor:ktor-http-jvm:$ktorVersion")
        implementation("ch.qos.logback:logback-classic:$logbackVersion")
        implementation("io.ktor:ktor-server-html-builder:$ktorVersion")
        implementation(project(":Update"))
        implementation(project(":System"))
    }
    sourceSets["jsMain"].dependencies {
        implementation(compose.web.core)
        implementation("io.ktor:ktor-client-core:$ktorVersion")
        implementation("io.ktor:ktor-client-js:$ktorVersion")
        implementation("io.ktor:ktor-client-serialization:$ktorVersion")
        implementation("io.ktor:ktor-client-websockets:$ktorVersion")
        implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
        implementation("dev.petuska:kmdc-button:$kmdcVersion")
        implementation("dev.petuska:kmdc-textfield:$kmdcVersion")
        implementation(project(":Localization"))
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
