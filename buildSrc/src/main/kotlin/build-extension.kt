import org.gradle.api.Project
import org.gradle.language.jvm.tasks.ProcessResources

val Project.modInfo: ModInfo get() = ModInfo(this)
fun Project.prop(key: String): String? = findProperty(key)?.toString()

fun ProcessResources.properties(files: Iterable<String>, vararg properties: Pair<String, Any>) {
    val map: MutableMap<String, Any> = mutableMapOf()
    for ((name, value) in properties) {
        inputs.property(name, value)
        addProperty(map, name, value as String)
    }
    filesMatching(files) {
        expand(map)
    }
}

@JvmInline
value class ModInfo(private val project: Project) {
    val id: String get() = requireNotNull(project.parent?.prop("mod.id")) { "Missing 'mod.id'" }
    val name: String get() = requireNotNull(project.parent?.prop("mod.name")) { "Missing 'mod.name'" }
    val description: String get() = requireNotNull(project.parent?.prop("mod.description")) { "Missing 'mod.description'" }
    val version: String get() = requireNotNull(project.parent?.prop("mod.version")) { "Missing 'mod.version'" }

    fun prop(key: String) = requireNotNull(project.prop("mod.$key")) { "Missing 'mod.$key'" }
    fun dep(key: String) = requireNotNull(project.prop("deps.$key")) { "Missing 'deps.$key'" }

    override fun toString(): String {
        return "ModInfo(project=$project, id='$id', name='$name', description='$description', version='$version')"
    }
}

private fun addProperty(map: MutableMap<String, Any>, key: String, value: String) {
    val keys = key.split(".")

    fun buildMap(map: MutableMap<String, Any>, keys: List<String>, value: String): MutableMap<String, Any> {
        when (keys.size) {
            1 -> map[keys[0]] = value
            else -> {
                val entry = map.computeIfAbsent(keys[0]) { mutableMapOf<String, Any>() }
                if (entry !is MutableMap<*, *>) error("Incompatible properties found while merging into a recursive map")
                @Suppress("UNCHECKED_CAST")
                val map1: MutableMap<String, Any> = entry as MutableMap<String, Any>;
                map[keys[0]] = buildMap(map1, keys.drop(1), value)
            }
        }
        return map;
    }

    buildMap(map, keys, value)
}