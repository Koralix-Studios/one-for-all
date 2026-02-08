plugins {
    id("java")
    id("fabric-loom")
}

val common = ModInfo(stonecutter.node.sibling("")!!.project)
val minecraft = stonecutter.current.project

val core = stonecutter.node.sibling("ofa-core")!!.project
val coreModInfo = ModInfo(core)

version = modInfo.version

base {
    archivesName.set("${modInfo.id}-$minecraft")
}

stonecutter {
    swaps["mod.id"] = "\"${modInfo.id}\";"
    swaps["mod.name"] = "\"${modInfo.name}\";"
    swaps["mod.description"] = "\"${modInfo.description}\";"
    swaps["mod.version"] = "\"${modInfo.version}\";"
}

project.evaluationDependsOn(core.path)

repositories {
    maven { url = uri("https://maven.fallenbreath.me/releases") }
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraft")
    mappings("net.fabricmc:yarn:$minecraft+build.${common.dep("fabric.yarn")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${common.dep("fabric.loader")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${common.dep("fabric.api")}+$minecraft")

    implementation(project(core.path, configuration = "namedElements"))
    implementation(core.sourceSets["client"].output)

    modCompileOnly("me.fallenbreath:conditional-mixin-fabric:0.6.4")
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
        ideConfigGenerated(false)
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
        "core.id" to coreModInfo.id,
        "core.name" to coreModInfo.name,
        "core.description" to coreModInfo.description,
        "core.version" to coreModInfo.version,
        "deps.core.id" to coreModInfo.id,
        "deps.core.version" to coreModInfo.version,
        "deps.fabric.yarn" to common.dep("fabric.yarn.dependency"),
        "deps.fabric.loader" to common.dep("fabric.loader.dependency"),
        "deps.fabric.api" to common.dep("fabric.api.dependency"),
        "deps.minecraft" to minecraft,
        "deps.java" to java.targetCompatibility.majorVersion
    )
}
