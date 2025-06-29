import net.fabricmc.loom.task.RemapJarTask

plugins {
    id("java")
    id("fabric-loom")
}

version = modInfo.version

val minecraft = stonecutter.current.project

val core: Project = stonecutter.node.sibling("ofa-core")!!.project
val base: Project = stonecutter.node.sibling("ofa-base")!!.project

val projects = arrayOf(core, base)

base {
    archivesName.set("${modInfo.id}-${stonecutter.current.project}")
}

stonecutter {
    swap("mod.id", "\"${modInfo.id}\";")
    swap("mod.name", "\"${modInfo.name}\";")
    swap("mod.description", "\"${modInfo.description}\";")
    swap("mod.version", "\"${modInfo.version}\";")
}

projects.forEach {
    project.evaluationDependsOn(it.path)
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraft")
    mappings("net.fabricmc:yarn:$minecraft+build.${modInfo.dep("fabric.yarn")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${modInfo.dep("fabric.loader")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${modInfo.dep("fabric.api")}+$minecraft")

    projects.forEach {
        implementation(project(it.path, configuration = "namedElements"))
        implementation(it.sourceSets["client"].output)
    }
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
    projects.forEach {
        val remapTask = it.tasks.getByName<RemapJarTask>("remapJar")
        dependsOn(remapTask)
        inputs.files(remapTask.archiveFile)
        nestedJars.from(remapTask.archiveFile)
    }
    addNestedDependencies.set(true)
}

tasks.processResources {
    properties(
        listOf("fabric.mod.json"),
        "mod.id" to modInfo.id,
        "mod.name" to modInfo.name,
        "mod.description" to modInfo.description,
        "mod.version" to modInfo.version,
        "deps.fabric.yarn" to modInfo.dep("fabric.yarn.dependency"),
        "deps.fabric.loader" to modInfo.dep("fabric.loader.dependency"),
        "deps.fabric.api" to modInfo.dep("fabric.api.dependency"),
        "deps.minecraft" to minecraft,
        "deps.java" to java.targetCompatibility.majorVersion
    )
}