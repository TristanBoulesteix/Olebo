@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    application
    alias(libs.plugins.kotlin.multiplatform)
    id("org.jetbrains.compose") version libs.versions.compose.get()
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
        implementation(libs.serialization)
    }
    sourceSets["jvmMain"].dependencies {
        implementation(libs.bundles.ktor.server)
        implementation(libs.logback)
        implementation(project(":Update"))
        implementation(project(":System"))
    }
    sourceSets["jsMain"].dependencies {
        implementation(devNpm("style-loader", npm.versions.style.loader.get()))
        implementation(devNpm("css-loader", npm.versions.css.loader.get()))
        implementation(devNpm("sass-loader", npm.versions.sass.loader.get()))
        implementation(devNpm("sass", npm.versions.sass.asProvider().get()))

        implementation(compose.web.core)
        implementation(libs.bundles.ktor.client.js)
        implementation(libs.bundles.kmdc)
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
