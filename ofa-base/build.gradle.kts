plugins {
    id("java")
    id("fabric-loom")
}

println(modInfo)

val common = ModInfo(stonecutter.node.sibling("")!!.project)
val minecraft = stonecutter.current.project

val core = stonecutter.node.sibling("ofa-core")!!.project

version = modInfo.prop("version")

base {
    archivesName.set("${modInfo.id}-$minecraft")
}

stonecutter {
    swap("mod.id", "\"${modInfo.prop("id")}\";")
    swap("mod.name", "\"${modInfo.prop("name")}\";")
    swap("mod.description", "\"${modInfo.prop("description")}\";")
    swap("mod.version", "\"${modInfo.prop("version")}\";")
}

project.evaluationDependsOn(core.path)

dependencies {
    minecraft("com.mojang:minecraft:$minecraft")
    mappings("net.fabricmc:yarn:$minecraft+build.${common.dep("fabric.yarn")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${common.dep("fabric.loader")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${common.dep("fabric.api")}+$minecraft")

    implementation(project(core.path, configuration = "namedElements"))
    implementation(core.sourceSets["client"].output)
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
    obj.properties(listOf("fabric.mod.json", "*.mixins.json"),
        "mod.id" to modInfo.id,
        "mod.name" to modInfo.name,
        "mod.description" to modInfo.description,
        "mod.version" to modInfo.version,
        "deps.fabric.yarn" to common.dep("fabric.yarn"),
        "deps.fabric.loader" to common.dep("fabric.loader"),
        "deps.fabric.api" to common.dep("fabric.api"),
        "deps.minecraft" to minecraft,
        "deps.java" to java.targetCompatibility.majorVersion
    )
}
