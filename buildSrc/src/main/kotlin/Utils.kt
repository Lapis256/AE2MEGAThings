import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.provider.Provider


@Suppress("unused")
enum class Order {
    NONE, BEFORE, AFTER;
}

@Suppress("unused")
enum class Side {
    CLIENT, SERVER, BOTH;
}

@Suppress("unused")
enum class DisplayTest {
    MATCH_VERSION, IGNORE_SERVER_VERSION, IGNORE_ALL_VERSION, NONE;
}

@Suppress("unused")
enum class DependencyType {
    REQUIRED, OPTIONAL, INCOMPATIBLE, DISCOURAGED;
}

@Suppress("unused")
data class ModDep(
    val id: String, val version: String, val type: DependencyType = DependencyType.REQUIRED, val ordering: Order = Order.NONE, val side: Side = Side.BOTH
)

@Suppress("unused")
fun buildDeps(
    vararg deps: ModDep
): String {
    return deps.joinToString(separator = "\n") { (id, version, type, ordering, side) ->
        """
            [[dependencies.${Constants.Mod.id}]]
            modId = "$id"
            versionRange = "[$version,)"
            type = "$type"
            ordering = "$ordering"
            side = "$side"
        """.trimIndent()
    }
}

@Suppress("unused")
fun extractVersionSegments(versionString: String, numberOfSegments: Int = 1) =
    versionString.split(".").take(numberOfSegments).joinToString(".")

@Suppress("unused")
fun extractVersionSegments(version: Provider<String>, numberOfSegments: Int = 1) =
    extractVersionSegments(version.get(), numberOfSegments)

@Suppress("unused")
fun DependencyHandler.variantOf(dependency: Provider<MinimalExternalModuleDependency>, classifier: String) =
    variantOf(dependency) { classifier(classifier) }
