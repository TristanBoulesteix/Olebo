plugins {
    kotlin("jvm") version "1.3.60"
}

group = "jdr.exia.updater"
version = "1.0-BETA"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    compile("org.apache.httpcomponents", "httpclient", "4.5.10")
    compile("org.json","json", "20190722")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

val jar by tasks.getting(Jar::class) {
    manifest {
        attributes["Main-Class"] = "jdr.exia.OleboKt"
    }
    from(configurations.compile.map { configuration ->
        configuration.asFileTree.fold(files().asFileTree) { collection, file ->
            if (file.isDirectory) collection else collection.plus(zipTree(file))
        }
    })
}