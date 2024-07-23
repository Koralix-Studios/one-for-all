plugins {
    id("dev.architectury.loom") version("1.7-SNAPSHOT")
}

class ModData {
    val id = property("mod.id").toString()
    val name = property("mod.name").toString()
    val version = property("mod.version").toString()
    val group = property("mod.group").toString()
    val minecraftDependency = property("minecraft.dependency").toString()
    val minMinecraft = property("minecraft.min").toString()
    val maxMinecraft = property("minecraft.max").toString()
}

class LoaderData {
    private val name = loom.platform.get().name.lowercase()
    val isFabric = name == "fabric"

    override fun toString(): String {
        return name
    }
}

class MinecraftVersionData {
    private val name = stonecutter.current.version.substringBeforeLast("-")
    val javaVersion = if (greaterThan("1.20.4")) 21 else 17

    fun greaterThan(version: String): Boolean {
        return stonecutter.compare(name, version.lowercase()) > 0
    }

    fun lessThan(version: String): Boolean {
        return stonecutter.compare(name, version.lowercase()) < 0
    }

    override fun toString(): String {
        return name
    }
}

val mod = ModData()
val loader = LoaderData()
val minecraftVersion = MinecraftVersionData()

version = "${mod.version}-$loader+$minecraftVersion"
group = mod.group
base {
    archivesName.set(mod.name)
}

repositories {
    mavenCentral()
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")

    testImplementation("it.unimi.dsi:fastutil:8.5.9")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

loom {
    splitEnvironmentSourceSets()

    mods {
        create(mod.id) {
            sourceSet(sourceSets["main"])
            sourceSet(sourceSets["client"])
        }
    }
}

tasks.withType<JavaCompile> {
    options.release = minecraftVersion.javaVersion
}

java {
    withSourcesJar()

    sourceCompatibility = JavaVersion.toVersion(minecraftVersion.javaVersion)
    targetCompatibility = JavaVersion.toVersion(minecraftVersion.javaVersion)
}

val buildAndCollect = tasks.register<Copy>("buildAndCollect") {
    group = "build"
    from(tasks.remapJar.get().archiveFile)
    into(rootProject.layout.buildDirectory.file("libs/${mod.version}"))
    dependsOn("build")
}

if (stonecutter.current.isActive) {
    rootProject.tasks.register("buildActive") {
        group = "project"
        dependsOn(buildAndCollect)
    }
}

if (loader.isFabric) {
    dependencies {
        modImplementation("net.fabricmc:fabric-loader:${property("fabric.loader")}")
        modImplementation("net.fabricmc.fabric-api:fabric-api:${property("fabric.api")}")

        mappings("net.fabricmc:yarn:$minecraftVersion+build.${property("fabric.yarn")}:v2")
    }

    tasks.processResources {
        val map = mapOf(
            "id" to mod.id,
            "name" to mod.name,
            "version" to mod.version,
            "group" to mod.group,
            "minecraft_dependency" to mod.minecraftDependency,
        )

        inputs.properties(map)
        filesMatching("fabric.mod.json") {
            expand(map)
        }
    }
}
