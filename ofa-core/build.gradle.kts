plugins {
    id("java")
    id("fabric-loom")
}

val common = ModInfo(stonecutter.node.sibling("")!!.project)
val minecraft = stonecutter.current.project

version = modInfo.version

base {
    archivesName.set("${modInfo.id}-$minecraft")
}

stonecutter {
    swap("mod.id", "\"${modInfo.id}\";")
    swap("mod.name", "\"${modInfo.id}\";")
    swap("mod.description", "\"${modInfo.id}\";")
    swap("mod.version", "\"${modInfo.id}\";")
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraft")
    mappings("net.fabricmc:yarn:$minecraft+build.${common.dep("fabric.yarn")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${common.dep("fabric.loader")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${common.dep("fabric.api")}+$minecraft")
}

loom {
    splitEnvironmentSourceSets()
    mods {
        create(modInfo.id) {
            sourceSet(sourceSets["main"])
            sourceSet(sourceSets["client"])
        }
    }
    decompilers {
        get("vineflower").apply {
            options.put("mark-corresponding-synthetics", "1")
        }
    }
    runConfigs.all {
        ideConfigGenerated(true)
        runDir = "../../run"
    }
}

java {
    withSourcesJar()
    withJavadocJar()
    val java = if (stonecutter.eval(minecraft, ">=1.20.5"))
        JavaVersion.VERSION_21
    else
        JavaVersion.VERSION_17
    sourceCompatibility = java
    targetCompatibility = java
}

tasks.named<ProcessResources>("processClientResources") {
    processResources(this)
}

tasks.processResources {
    processResources(this)
}

fun processResources(obj: ProcessResources) {
    obj.properties(
        listOf(
            "fabric.mod.json",
            "*.mixins.json",
            "assets/${modInfo.id}/lang/*.json"
        ),
        "mod.id" to modInfo.id,
        "mod.name" to modInfo.name,
        "mod.description" to modInfo.description,
        "mod.version" to modInfo.version,
        "common.id" to common.id,
        "common.name" to common.name,
        "common.description" to common.description,
        "common.version" to common.version,
        "deps.fabric.yarn" to common.dep("fabric.yarn"),
        "deps.fabric.loader" to common.dep("fabric.loader"),
        "deps.fabric.api" to common.dep("fabric.api"),
        "deps.minecraft" to minecraft,
        "deps.java" to java.targetCompatibility.majorVersion
    )
}
