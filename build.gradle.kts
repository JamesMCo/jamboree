import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.fabric.loom)
    `maven-publish`
    java
}

group = property("maven_group")!!
version = property("mod_version")!!

repositories {
    maven("https://maven.terraformersmc.com") {
        name = "Terraformers"
    }
    maven("https://maven.isxander.dev/releases") {
        name = "Xander Maven"
    }
    maven("https://maven.noxcrew.com/public") {
        name = "Noxcrew Public Maven Repository"
    }
    maven("https://maven.enginehub.org/repo/") {
        name = "EngineHub"
    }
    exclusiveContent {
        forRepository {
            maven("https://api.modrinth.com/maven") {
                name = "Modrinth"
            }
        }
        filter {
            includeGroup("maven.modrinth")
        }
    }
}

val transitiveInclude: Configuration by configurations.creating {
    exclude(group = "com.mojang")
    exclude(group = "org.jetbrains.kotlin")
    exclude(group = "org.jetbrains.kotlinx")
}

dependencies {
    minecraft(libs.minecraft)
    mappings(loom.officialMojangMappings())
    modImplementation(libs.fabric.loader)

    modImplementation(libs.fabric.kotlin)
    modImplementation(libs.fabric.api)
    modImplementation(libs.modmenu)
    modImplementation(libs.noxesium)
    modImplementation(libs.yacl)

    modCompileOnly(libs.mccfishingmessages)

    implementation(libs.slf4j)

    transitiveInclude.resolvedConfiguration.resolvedArtifacts.forEach {
        include(it.moduleVersion.id.toString())
    }
}

tasks {
    processResources {
        inputs.property("version", project.version)
        filesMatching("fabric.mod.json") {
            expand(getProperties() + mutableMapOf(
                "version" to project.version,
                "mccfishingmessages_version" to libs.mccfishingmessages.get(),
                "minecraft_version" to libs.versions.minecraft.get(),
                "fabric_kotlin_version" to libs.versions.fabric.kotlin.get(),
                "fabric_loader_version" to libs.versions.fabric.loader.get(),
                "noxesium_version" to libs.versions.noxesium.get(),
                "yacl_version" to libs.versions.yacl.get()
            ))
        }
    }

    jar {
        from("LICENSE")
    }

    remapJar {
        archiveFileName = "jamboree-${version}+${libs.versions.minecraft.get()}.jar"
    }

    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                artifact(remapJar) {
                    builtBy(remapJar)
                }
                artifact(kotlinSourcesJar) {
                    builtBy(remapSourcesJar)
                }
            }
        }
    }

    compileKotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
            optIn.add("kotlin.time.ExperimentalTime")
        }
    }
}
