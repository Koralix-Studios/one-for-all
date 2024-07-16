plugins {
    id("fabric-loom") version("1.7-SNAPSHOT") apply(false)
}

repositories {
    mavenCentral()
}

val fabricApiVersion: String by extra
val fabricLoaderVersion: String by extra
val githubUrl: String by extra
val minecraftVersion: String by extra
val minecraftVersionRange: String by extra
val modAuthor: String by extra
val modDescription: String by extra
val modGroup: String by extra
val modId: String by extra
val modJavaVersion: String by extra
val modName: String by extra
val specVersion: String by extra

subprojects {
    val buildNumber = project.findProperty("BUILD_NUMBER")?.toString() ?: "9999"

    group = modGroup
    version = "$specVersion-$buildNumber"

    tasks.withType<Javadoc> {
        val standardJavadocDocletOptions = options as StandardJavadocDocletOptions
        standardJavadocDocletOptions.addStringOption("Xdoclint:none", "-quiet")
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
//        options.release.set(JavaLanguageVersion.of(modJavaVersion).asInt())
    }

    tasks.withType<Jar> {
        manifest {
            attributes(
                "Specification-Title" to modName,
                "Specification-Version" to specVersion,
                "Specification-Vendor" to modAuthor,
                "Implementation-Title" to name,
                "Implementation-Version" to archiveVersion,
                "Implementation-Vendor" to modAuthor
            )
        }
    }

    tasks.withType<ProcessResources> {
        // this will ensure that this task is redone when the versions change.
        inputs.property("version", version)

        filesMatching(listOf("META-INF/mods.toml", "META-INF/neoforge.mods.toml", "pack.mcmeta", "fabric.mod.json")) {
            expand(mapOf(
//                "modrinthHomepageUrl" to modrinthHomepageUrl,
                "fabricApiVersion" to fabricApiVersion,
                "fabricLoaderVersion" to fabricLoaderVersion,
                "githubUrl" to githubUrl,
//                "minecraftVersion" to minecraftVersion,
//                "minecraftVersionRange" to minecraftVersionRange,
                "modAuthor" to modAuthor,
                "modDescription" to modDescription,
                "modId" to modId,
//                "modJavaVersion" to modJavaVersion,
                "modName" to modName,
                "version" to version,
            ))
        }
    }

    // Activate reproducible builds
    // https://docs.gradle.org/current/userguide/working_with_files.html#sec:reproducible_archives
    tasks.withType<AbstractArchiveTask>().configureEach {
        isPreserveFileTimestamps = false
        isReproducibleFileOrder = true
    }
}
