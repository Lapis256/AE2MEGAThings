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

val modId = Constants.Mod.id
val mcVersion: String = libs.versions.minecraft.get()
val kffVersion: String = libs.versions.kotlinForForge.get()
val jdkVersion = 21

val exportMixin = true
val loadAppMek = true

base {
    archivesName = "${project.name}-$mcVersion"
    version = Constants.Mod.version
    group = Constants.Mod.group
}

neoForge {
    version = libs.versions.neoforge.get()

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
            systemProperty("neoforge.enabledGameTestNamespaces", modId)
            jvmArgument("-Dmixin.debug.export=$exportMixin")
        }

        create("server") {
            server()
            gameDirectory.set(file("run-server"))
            programArgument("--nogui")
            systemProperty("neoforge.enabledGameTestNamespaces", modId)
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
    localRuntime(libs.mekanism)
    // localRuntime(variantOf(libs.mekanism, "generators"))
    localRuntime("curse.maven:mekanism-generators-268566:6018309")
    if (loadAppMek) {
        localRuntime(libs.appmek)
    }
}

val modDependencies = buildDeps(
    ModDep("neoforge", extractVersionSegments(libs.versions.neoforge, 2)),
    ModDep("minecraft", mcVersion),
    ModDep("kotlinforforge", kffVersion),
    ModDep("ae2", "19.0.20-beta", ordering = Order.AFTER),
    ModDep("megacells", "2.1", ordering = Order.AFTER),
    ModDep("ae2things", "1.2", ordering = Order.AFTER),
    ModDep("appmek", "1.4", ordering = Order.AFTER, type = DependencyType.OPTIONAL),
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

        "dependencies" to modDependencies
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

neoForge.ideSyncTask(generateModMetadata)

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true

        resourceDirs.add(file("src/main/templates"))
    }
}
