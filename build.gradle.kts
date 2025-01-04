plugins {
    `maven-publish`
    id("fabric-loom")
    //id("dev.kikugie.j52j")
    //id("me.modmuss50.mod-publish-plugin")
}

class ModData {
    val id = property("mod.id").toString()
    val name = property("mod.name").toString()
    val version = property("mod.version").toString()
    val group = property("mod.group").toString()
}

class MinecraftVersionData {
    private val name = stonecutter.current.version
    val javaVersion = if (greaterThan("1.20.4")) 21 else 17
    val dependency = property("minecraft.dependency").toString()
    val min = property("minecraft.min").toString()
    val max = property("minecraft.max").toString()
    val title = property("minecraft.title").toString()
    val targets = property("minecraft.targets").toString()

    fun greaterThan(version: String): Boolean {
        return stonecutter.eval(name, ">${version.lowercase()}")
    }

    fun lessThan(version: String): Boolean {
        return stonecutter.eval(name, "<${version.lowercase()}")
    }

    override fun toString(): String {
        return name
    }
}

class FabricData {
    val loader = property("fabric.loader").toString()
    val api = property("fabric.api").toString()
    val yarn = property("fabric.yarn").toString()
}

class ModDependencies {
    operator fun get(name: String) = property("deps.$name").toString()
}

val mod = ModData()
val minecraftVersion = MinecraftVersionData()
val fabric = FabricData()
val deps = ModDependencies()

version = "${mod.version}+$minecraftVersion"
group = mod.group
base { archivesName.set(mod.id) }

loom {
    accessWidenerPath = file("../../src/main/resources/${mod.id}.accesswidener")

    splitEnvironmentSourceSets()

    mods {
        create(mod.id) {
            sourceSet(sourceSets["main"])
            sourceSet(sourceSets["client"])
        }
    }
}

repositories {
    fun strictMaven(url: String, alias: String, vararg groups: String) = exclusiveContent {
        forRepository { maven(url) { name = alias } }
        filter { groups.forEach(::includeGroup) }
    }
    strictMaven("https://www.cursemaven.com", "CurseForge", "curse.maven")
    strictMaven("https://api.modrinth.com/maven", "Modrinth", "maven.modrinth")
}

dependencies {
    fun fapi(vararg modules: String) = modules.forEach {
        modImplementation(fabricApi.module(it, fabric.api))
    }

    minecraft("com.mojang:minecraft:$minecraftVersion")
    mappings("net.fabricmc:yarn:$minecraftVersion+build.${fabric.yarn}:v2")
    modImplementation("net.fabricmc:fabric-loader:${fabric.loader}")

//    fapi(
//        // Add modules from https://github.com/FabricMC/fabric
//        "fabric-api-base",
//        "fabric-lifecycle-events-v1",
//        "fabric-command-api-v1",
//        "fabric-networking-api-v1",
//    )
    modImplementation("net.fabricmc.fabric-api:fabric-api:${fabric.api}")
}

loom {
    decompilers {
        get("vineflower").apply { // Adds names to lambdas - useful for mixins
            options.put("mark-corresponding-synthetics", "1")
        }
    }

    runConfigs.all {
        ideConfigGenerated(true)
        vmArgs("-Dmixin.debug.export=true")
        runDir = "run"
    }
}

java {
    withSourcesJar()
    val java = JavaVersion.toVersion(minecraftVersion.javaVersion)
    targetCompatibility = java
    sourceCompatibility = java
}

tasks.processResources {
    inputs.property("id", mod.id)
    inputs.property("name", mod.name)
    inputs.property("version", mod.version)
    inputs.property("minecraft_dependency", minecraftVersion.dependency)

    val map = mapOf(
        "id" to mod.id,
        "name" to mod.name,
        "version" to mod.version,
        "minecraft_dependency" to minecraftVersion.dependency,
    )

    filesMatching("fabric.mod.json") { expand(map) }
}

tasks.register<Copy>("buildAndCollect") {
    group = "build"
    from(tasks.remapJar.get().archiveFile)
    into(rootProject.layout.buildDirectory.file("libs/${mod.version}"))
    dependsOn("build")
}
