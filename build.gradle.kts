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

sourceSets {
    main {
        resources {
            srcDir("src/generated/resources")
            exclude("**/.cache")
        }
    }
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
            gameDirectory.dir("run")
            systemProperty("neoforge.enabledGameTestNamespaces", modId)
            jvmArgument("-Dmixin.debug.export=$exportMixin")
        }

        create("server") {
            server()
            gameDirectory.dir("run-server")
            programArgument("--nogui")
            systemProperty("neoforge.enabledGameTestNamespaces", modId)
            jvmArgument("-Dmixin.debug.export=$exportMixin")
        }

        create("data") {
            data()
            gameDirectory.dir("run-data")
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
    modRuntimeOnly(variantOf(libs.mekanism, "all"))
    modRuntimeOnly(libs.clothConfig)
    if (loadAppMek) {
        modRuntimeOnly(libs.appmek)
    }

    annotationProcessor(variantOf(libs.mixin, "processor"))
    libs.mixinExtrasCommon.let {
        annotationProcessor(it)
        compileOnly(it)
    }
    libs.mixinExtrasForge.let {
        jarJar(variantOf(it, "slim")) {
            version {
                val version = it.get().version.toString()
                strictly("[$version,)")
                prefer(version)
            }
        }
        implementation(it)
    }
}

val modDependencies = buildDeps(
    ModDep("forge", extractVersionSegments(forgeVersion)),
    ModDep("minecraft", mcVersion),
    ModDep("kotlinforforge", kffVersion),
    ModDep("ae2", extractVersionSegments(libs.versions.ae2)),
    ModDep("megacells", "2.1", ordering = Order.AFTER),
    ModDep("ae2things", "1.2"),
    ModDep("appmek", "1.4", mandatory = false),
)

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

        val prop = mapOf(
            "version" to version,
            "group" to project.group,
            "minecraft_version" to mcVersion,
            "mod_loader" to "kotlinforforge",
            "mod_loader_version_range" to "[$kffVersion,)",
            "mod_name" to Constants.Mod.name,
            "mod_author" to Constants.Mod.author,
            "mod_id" to Constants.Mod.id,
            "license" to Constants.Mod.license,
            "description" to Constants.Mod.description,
            "display_url" to Constants.Mod.repositoryUrl,
            "issue_tracker_url" to Constants.Mod.issueTrackerUrl,

            "dependencies" to modDependencies
        )

        filesMatching(listOf("pack.mcmeta", "META-INF/mods.toml", "*.mixins.json")) {
            filter<ReplaceTokens>("beginToken" to "\${", "endToken" to "}", "tokens" to prop)
        }
        inputs.properties(prop)
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
                "FMLAT" to "accesstransformer.cfg",
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
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}
