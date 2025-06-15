plugins {
    id("dev.kikugie.stonecutter")
    id("fabric-loom") version "1.10-SNAPSHOT" apply false
}
stonecutter active "1.21.5"

stonecutter registerChiseled tasks.register("chiseledBuild", stonecutter.chiseled) {
    group = "project"
    ofTask("build")
}

stonecutter registerChiseled tasks.register("chiseledClean", stonecutter.chiseled) {
    group = "project"
    ofTask("clean")
}

allprojects {
    repositories {
        mavenLocal()
    }
}