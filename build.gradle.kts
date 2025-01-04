import org.apache.tools.ant.filters.ReplaceTokens
import java.text.SimpleDateFormat
import java.util.*

plugins {
    id("java")
    id("java-library")
    id("idea")

    id("localRuntime")

    alias(libs.plugins.kotlin)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.moddev)
}

val kffVersion: String = extractVersionSegments(libs.versions.kotlinForForge)
val modId = Constants.Mod.id
val minecraftVersion: String = libs.versions.minecraft.get()
val jdkVersion = 21

val exportMixin = true
val loadAppMek = true

base {
    archivesName = "${project.name}-$minecraftVersion"
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

neoForge {
    version = libs.versions.neoforge.get()

    file("src/main/resources/META-INF/accesstransformer.cfg").takeIf(File::exists)?.let {
        println("Adding access transformer: $it")
        setAccessTransformers(it)
    }

    parchment {
        mappingsVersion = libs.versions.parchmentmc.get()
        minecraftVersion = extractVersionSegments(libs.versions.minecraft, 2)
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

            logLevel = org.slf4j.event.Level.DEBUG
        }
    }

    mods {
        create(modId) {
            sourceSet(sourceSets["main"])
        }
    }
}

repositories {
    mavenCentral()
    maven {
        name = "Kotlin for Forge"
        url = uri("https://thedarkcolour.github.io/KotlinForForge/")
    }
    maven {
        name = "JEI"
        url = uri("https://modmaven.dev/")
    }
    maven {
        name = "Curse Maven"
        url = uri("https://cursemaven.com")
    }
}

dependencies {
    implementation(libs.kotlinForForge)
    implementation(libs.ae2)
    implementation(libs.megacells)
    implementation(libs.ae2things)

    compileOnly(variantOf(libs.mekanism, "api"))
    compileOnly(libs.appmek)

    localRuntime(libs.jei)
    localRuntime(variantOf(libs.mekanism, "all"))
    if (loadAppMek) {
        localRuntime(libs.appmek)
    }
}

val modDependencies = buildDeps(
    ModDep("neoforge", extractVersionSegments(libs.versions.neoforge, 2)),
    ModDep("minecraft", minecraftVersion),
    ModDep("kotlinforforge", kffVersion),
    ModDep("ae2", "19.0.20-beta", ordering = Order.AFTER),
    ModDep("megacells", "2.1", ordering = Order.AFTER),
    ModDep("ae2things", "1.2", ordering = Order.AFTER),
    ModDep("appmek", "1.4", ordering = Order.AFTER, type = DependencyType.OPTIONAL),
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
            "minecraft_version" to minecraftVersion,
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

        filesMatching(listOf("pack.mcmeta", "META-INF/neoforge.mods.toml", "*.mixins.json")) {
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
                "Built-On-Minecraft" to minecraftVersion,
                "FMLAT" to "accesstransformer.cfg",
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
