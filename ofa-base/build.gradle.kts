import net.fabricmc.loom.task.RemapJarTask

plugins {
    id("java")
    id("fabric-loom")
}

class ModInfo(
    val id: String,
    val name: String,
    val description: String,
    val version: String,
) {
    constructor() : this(
        id = properties["mod.id"] as String,
        name = properties["mod.name"] as String,
        description = properties["mod.description"] as String,
        version = properties["mod.version"] as String,
    )
}

val modInfo: ModInfo = ModInfo()
val core = project(":ofa-core:${stonecutter.current.project}")

version = properties["mod.version"] as String

base {
    archivesName.set("${modInfo.id}-${stonecutter.current.project}")
}

stonecutter {
    swap("mod.id", "\"${properties["mod.id"] as String}\";")
    swap("mod.name", "\"${properties["mod.name"] as String}\";")
    swap("mod.description", "\"${properties["mod.description"] as String}\";")
    swap("mod.version", "\"${properties["mod.version"] as String}\";")
}

project.evaluationDependsOn(":ofa-core:${stonecutter.current.project}")

dependencies {
//    val clientImplementation = configurations.getByName("clientImplementation")

    minecraft("com.mojang:minecraft:${stonecutter.current.project}")
    mappings("net.fabricmc:yarn:${stonecutter.current.version}+build.${property("deps.fabric.yarn")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric.loader")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fabric.api")}+${stonecutter.current.version}")

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
    val java = if (stonecutter.eval(stonecutter.current.version, ">=1.20.5"))
        JavaVersion.VERSION_21
    else
        JavaVersion.VERSION_17
    sourceCompatibility = java
    targetCompatibility = java
}

tasks.named<RemapJarTask>("remapJar") {
    val core = project(":ofa-core:${stonecutter.current.project}").tasks.getByName<RemapJarTask>("remapJar")
    dependsOn(core)
    inputs.files(core.archiveFile)
    nestedJars.from(core.archiveFile)
    addNestedDependencies.set(true)
}

tasks.named<ProcessResources>("processClientResources") {
    processResources(this)
}

tasks.processResources {
    processResources(this)
}

fun processResources(obj: ProcessResources) {
    val map = mapOf(
        "mod" to modInfo,
        "deps" to getDeps(),
    )

    obj.filesMatching("fabric.mod.json") { expand(map) }
    obj.filesMatching("*.mixins.json") { expand(map) }
}

fun getDeps(): Map<String, Any> {
    val deps = mutableMapOf<String, Any>()
    properties.forEach { (key, value) ->
        if (key.startsWith("deps.")) {
            val replace = key.endsWith(".dependency")
            val name = if (replace) {
                key.substring("deps.".length, key.length - ".dependency".length)
            } else {
                key.substring("deps.".length)
            }
            val map = createRecursiveMap(name, value as String)
            mergeRecursiveMaps(deps, map, replace)
        }
    }
    deps.putIfAbsent("minecraft", stonecutter.current.version)
    deps.putIfAbsent("java", java.targetCompatibility.majorVersion)
    return deps
}

fun createRecursiveMap(key: String, value: String): MutableMap<String, Any> {
    val keys = key.split(".")
    val result = mutableMapOf<String, Any>()

    fun buildMap(keys: List<String>, value: String): MutableMap<String, Any> {
        return when (keys.size) {
            1 -> mutableMapOf(keys[0] to value)
            else -> mutableMapOf(keys[0] to buildMap(keys.drop(1), value))
        }
    }

    return buildMap(keys, value).also { result.putAll(it) }
}

fun mergeRecursiveMaps(map1: MutableMap<String, Any>, map2: Map<String, Any>, replace: Boolean) {
    map2.forEach { (key, value) ->
        when {
            value is Map<*, *> && map1[key] is Map<*, *> -> {
                @Suppress("UNCHECKED_CAST")
                mergeRecursiveMaps(
                    map1[key] as MutableMap<String, Any>,
                    value as MutableMap<String, Any>,
                    replace
                )
            }
            else -> if (key !in map1 || replace)
                map1[key] = value
        }
    }
}
