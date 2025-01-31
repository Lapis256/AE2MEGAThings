pluginManagement {
    repositories {
        maven {
            name = "NeoForged"
            url = uri("https://maven.neoforged.net/releases")
        }
        maven {
            name = "ModPublisher"
            url = uri("https://maven.firstdarkdev.xyz/releases")
        }
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
}

rootProject.name = file("buildSrc/src/main/kotlin/Constants.kt").readLines()
    .map(String::trim)
    .find { it.startsWith("const val name") }
    ?.substringAfter('"')
    ?.substringBefore('"')
    ?.replace(" ", "")
    ?: throw IllegalStateException("Failed to find mod name")
