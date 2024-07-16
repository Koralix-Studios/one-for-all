pluginManagement {
    repositories {
        maven {
            name = "Fabric"
            url = uri("https://maven.fabricmc.net/")
        }
        gradlePluginPortal()
    }
}

val minecraftVersion: String by extra

rootProject.name = "oneforall-${minecraftVersion}"
include("core")
