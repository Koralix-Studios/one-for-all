import dev.kikugie.stonecutter.StonecutterSettings

pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/")
        maven("https://maven.architectury.dev")
        maven("https://maven.minecraftforge.net/")
        maven("https://maven.kikugie.dev/releases/")
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("dev.kikugie.stonecutter") version("0.4.2")
}

fun getProperty(name: String): String? {
    return settings.extra[name] as? String
}

fun getVersions(name: String): Set<String> {
    return getProperty(name)!!.split(",").map { it.trim() }.toSet()
}

val versions = mapOf(
    "fabric" to getVersions("fabric.versions"),
)

val sharedVersions = versions.map { entry ->
    val loader = entry.key
    entry.value.map { "$it-$loader" }
}.flatten().toSet()

extensions.configure<StonecutterSettings> {
    kotlinController = true
    centralScript = "build.gradle.kts"

    shared {
        versions(sharedVersions)
    }

    create(rootProject)
}

rootProject.name = "oneforall"
