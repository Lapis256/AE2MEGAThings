import com.hypherionmc.modpublisher.plugin.ModPublisherGradleExtension
import com.hypherionmc.modpublisher.properties.CurseEnvironment
import com.hypherionmc.modpublisher.properties.ModLoader
import com.hypherionmc.modpublisher.properties.ReleaseType
import org.apache.tools.ant.filters.ReplaceTokens
import java.text.SimpleDateFormat
import java.util.*

plugins {
    id("java")
    id("java-library")
    id("idea")

    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.moddev)
    alias(libs.plugins.modPublisher)
}

val modId = Constants.Mod.id
val mcVersion: String = libs.versions.minecraft.get()
val forgeVersion: String = libs.versions.forge.get()
val kffVersion: String = libs.versions.kotlinForForge.get()
val jdkVersion = 17

val exportMixin = true
val loadAppMek = true

base {
    archivesName = "${project.name}-$mcVersion"
    version = Constants.Mod.version
    group = Constants.Mod.group
}

legacyForge {
    version = "$mcVersion-$forgeVersion"

    validateAccessTransformers = true

    file("src/main/resources/META-INF/accesstransformer.cfg").takeIf(File::exists)?.let {
        println("Adding access transformer: $it")
        setAccessTransformers(it)
    }

    parchment {
        mappingsVersion = libs.versions.parchmentmc.get()
        minecraftVersion = mcVersion
    }

    runs {
        create("client") {
            client()
            gameDirectory.set(file("run"))
            systemProperty("forge.enabledGameTestNamespaces", modId)
            jvmArgument("-Dmixin.debug.export=$exportMixin")
        }

        create("server") {
            server()
            gameDirectory.set(file("run-server"))
            programArgument("--nogui")
            systemProperty("forge.enabledGameTestNamespaces", modId)
            jvmArgument("-Dmixin.debug.export=$exportMixin")
        }

        create("data") {
            data()
            gameDirectory.set(file("run-data"))
            programArguments.addAll(
                "--mod",
                modId,
                "--all",
                "--output",
                file("src/generated/resources/").absolutePath,
                "--existing",
                file("src/main/resources/").absolutePath
            )
        }

        configureEach {
            systemProperty("forge.logging.markers", "REGISTRIES")

            jvmArgument("-XX:+AllowEnhancedClassRedefinition")

            logLevel = org.slf4j.event.Level.DEBUG
        }
    }

    mods {
        create(modId) {
            sourceSet(sourceSets["main"])
        }
    }
}

mixin {
    add(sourceSets["main"], "${modId}.refmap.json")

    config("${modId}.mixins.json")
}

repositories {
    mavenCentral()
    maven {
        name = "Kotlin for Forge"
        url = uri("https://thedarkcolour.github.io/KotlinForForge/")
    }
    maven {
        name = "Sponge / Mixin"
        url = uri("https://repo.spongepowered.org/repository/maven-public/")
    }
    maven {
        name = "JEI / AE2"
        url = uri("https://modmaven.dev/")
    }
    maven {
        name = "Cloth Config"
        url = uri("https://maven.shedaniel.me/")
    }
    maven {
        name = "Curse Maven"
        url = uri("https://cursemaven.com")
    }
}

dependencies {
    implementation(libs.kotlinForForge)
    modImplementation(libs.ae2)
    modImplementation(libs.megacells)
    modImplementation(libs.ae2things)

    modCompileOnly(variantOf(libs.mekanism, "api"))
    modCompileOnly(libs.appmek)

    modRuntimeOnly(libs.jei)
    modRuntimeOnly(libs.mekanism)
    // modRuntimeOnly(variantOf(libs.mekanism, "generators"))
    modRuntimeOnly("curse.maven:mekanism-generators-268566:3875978")
    if (loadAppMek) {
        modRuntimeOnly(libs.appmek)
    }

    annotationProcessor(variantOf(libs.mixin, "processor"))
    libs.mixinExtrasCommon.let {
        annotationProcessor(it)
        compileOnly(it)
    }
    libs.mixinExtrasForge.let {
        jarJar(it) {
            version {
                val version = it.get().version.toString()
                strictly("[$version,)")
                prefer(version)
            }
        }
        implementation(it)
    }
}

val modDependencies = listOf(
    ModDep("forge", extractVersionSegments(forgeVersion)),
    ModDep("minecraft", mcVersion),
    ModDep("kotlinforforge", kffVersion),
    ModDep("ae2", extractVersionSegments(libs.versions.ae2), ordering = Order.AFTER),
    ModDep("megacells", "1.4", ordering = Order.AFTER),
    ModDep("ae2things", "1.0", ordering = Order.AFTER),
    ModDep("appmek", "1.2", mandatory = false, ordering = Order.AFTER),
)

val generateModMetadata by tasks.registering(ProcessResources::class) {
    val replaceProperties = mapOf(
        "version" to version,
        "group" to project.group,
        "minecraft_version" to mcVersion,
        "mod_loader" to "kotlinforforge",
        "mod_loader_version_range" to "[$kffVersion,)",
        "mod_name" to Constants.Mod.name,
        "mod_author" to Constants.Mod.author,
        "mod_id" to modId,
        "license" to Constants.Mod.license,
        "description" to Constants.Mod.description,
        "display_url" to Constants.Mod.repositoryUrl,
        "issue_tracker_url" to Constants.Mod.issueTrackerUrl,

        "dependencies" to buildDeps(*modDependencies.toTypedArray())
    )

    inputs.properties(replaceProperties)
    filter<ReplaceTokens>("beginToken" to "\${", "endToken" to "}", "tokens" to replaceProperties)
    from("src/main/templates")
    into("build/generated/sources/modMetadata")
    rename("template(\\..+)?.mixins.json", "${modId}$1.mixins.json")
}

tasks {
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release = jdkVersion
    }

    java {
        withSourcesJar()
        toolchain {
            languageVersion = JavaLanguageVersion.of(jdkVersion)
            vendor = JvmVendorSpec.JETBRAINS
        }
        JavaVersion.toVersion(jdkVersion).let {
            sourceCompatibility = it
            targetCompatibility = it
        }
    }

    processResources {
        from(rootProject.file("LICENSE")) {
            rename { "LICENSE_${Constants.Mod.id}" }
        }
        dependsOn(generateModMetadata)
    }

    jar {
        manifest {
            attributes(
                "Specification-Title" to Constants.Mod.name,
                "Specification-Vendor" to Constants.Mod.author,
                "Specification-Version" to version,
                "Implementation-Title" to project.name,
                "Implementation-Version" to version,
                "Implementation-Vendor" to Constants.Mod.author,
                "Implementation-Timestamp" to SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(Date()),
                "Timestamp" to System.currentTimeMillis(),
                "Built-On-Java" to "${System.getProperty("java.vm.version")} (${System.getProperty("java.vm.vendor")})",
                "Built-On-Minecraft" to mcVersion,
                "MixinConfigs" to "${modId}.mixins.json"
            )
        }
    }

    named<Jar>("sourcesJar") {
        from(rootProject.file("LICENSE")) {
            rename { "LICENSE_${Constants.Mod.id}" }
        }
    }

    named<Wrapper>("wrapper").configure {
        distributionType = Wrapper.DistributionType.BIN
    }

    named { it.startsWith("publish") }.forEach {
        it.notCompatibleWithConfigurationCache("ModPublisher plugin is not compatible with configuration cache")
    }
}

sourceSets {
    main {
        resources {
            srcDirs(
                "src/generated/resources",
                generateModMetadata.get().outputs.files
            )
            exclude("**/.cache")
        }
    }
}

legacyForge.ideSyncTask(generateModMetadata)

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true

        resourceDirs.add(file("src/main/templates"))
    }
}

fun ModPublisherGradleExtension.Dependencies.fromModDependencies(modDependencies: List<ModDep>) {
    modDependencies.filter {
        it.id != "minecraft" && it.id != "forge"
    }.forEach {
        if (it.mandatory) {
            required(it.id)
        } else {
            optional(it.id)
        }
    }
}

publisher {
    apiKeys {
        curseforge(System.getenv("CURSE_TOKEN"))
//        modrinth(System.getenv("MODRINTH_TOKEN"))
    }

    setReleaseType(ReleaseType.RELEASE)
    setLoaders(ModLoader.FORGE)
    setCurseEnvironment(CurseEnvironment.BOTH)

    debug.set(System.getenv("PUBLISHER_DEBUG") == "true")
    curseID.set(Constants.Publisher.curseforgeProjectId)
//    modrinthID.set(Constants.Publisher.modrinthProjectId)
    changelog.set(System.getenv("CHANGELOG") ?: "No changelog provided")
    projectVersion.set("${project.version}")
    displayName.set("[$mcVersion] v${project.version}")
    setGameVersions(mcVersion)
    setJavaVersions(jdkVersion)
    artifact.set(tasks.jar)

    curseDepends {
        required("applied-energistics-2", "ae2-things-forge", "mega-cells", "kotlin-for-forge")
        optional("applied-mekanistics")
    }

//    modrinthDepends {
//        required("applied-energistics-2", "ae2-things-forge", "mega-cells", "kotlin-for-forge")
//        optional("applied-mekanistics")
//    }
}
