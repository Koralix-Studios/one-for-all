plugins {
    id("java")
}

repositories {
    mavenCentral()
}

val jUnitVersion: String by extra
val minecraftVersion: String by extra
val modId: String by extra
val modJavaVersion: String by extra

dependencies {
    testImplementation(
        group = "org.junit.jupiter",
        name = "junit-jupiter-api",
        version = jUnitVersion
    )
    testRuntimeOnly(
        group = "org.junit.jupiter",
        name = "junit-jupiter-engine",
        version = jUnitVersion
    )
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(modJavaVersion))
    }
    withSourcesJar()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    javaToolchains {
        compilerFor {
            languageVersion.set(JavaLanguageVersion.of(modJavaVersion))
        }
    }
}

val sourcesJarTask = tasks.named<Jar>("sourcesJar")

val baseArchivesName = "${modId}-${minecraftVersion}-core"
base {
    archivesName.set(baseArchivesName)
}

artifacts {
    archives(tasks.jar.get())
    archives(sourcesJarTask.get())
}
