import com.google.gson.Gson
import com.google.gson.JsonObject
import com.matthewprenger.cursegradle.*
import java.io.InputStreamReader
import java.time.Instant
import java.time.format.DateTimeFormatter

fun property(key: String) = project.findProperty(key).toString()
fun optionalProperty(key: String) = project.findProperty(key)?.toString()

plugins {
    id("java")
    id("net.minecraftforge.gradle")
    id("org.parchmentmc.librarian.forgegradle")
    id("org.spongepowered.mixin")
    id("idea")
    id("maven-publish")
    id("com.matthewprenger.cursegradle") version "1.4.0"
    id("com.modrinth.minotaur") version "2.+"
}

repositories {
    maven("https://harleyoconnor.com/maven")
}

val modName = property("modName")
val modId = property("modId")
val modVersion = property("modVersion")
val mcVersion = property("mcVersion")

version = "$mcVersion-$modVersion"
group = property("group")

minecraft {
    mappings("parchment", "${property("mappingsVersion")}-1.18.2")

    runs {
        create("client") {
            workingDirectory = file("run").absolutePath

            property("forge.logging.markers", "SCAN,REGISTRIES,REGISTRYDUMP")
            property("forge.logging.console.level", "debug")

            property("mixin.debug.export", "true")
            property("mixin.env.remapRefMap", "true")
            property("mixin.env.refMapRemappingFile", "${buildDir}/createSrgToMcp/output.srg")

            if (project.hasProperty("mcUuid")) {
                args("--uuid", property("mcUuid"))
            }
            if (project.hasProperty("mcUsername")) {
                args("--username", property("mcUsername"))
            }
            if (project.hasProperty("mcAccessToken")) {
                args("--accessToken", property("mcAccessToken"))
            }

            args("-mixin.config=$modId.mixins.json")

            mods {
                create(modId) {
                    source(sourceSets.main.get())
                }
            }
        }

        create("server") {
            workingDirectory = file("run").absolutePath

            property("forge.logging.markers", "SCAN,REGISTRIES,REGISTRYDUMP")
            property("forge.logging.console.level", "debug")

            property("mixin.debug.export", "true")
            property("mixin.env.remapRefMap", "true")
            property("mixin.env.refMapRemappingFile", "${buildDir}/createSrgToMcp/output.srg")

            args("-mixin.config=$modId.mixins.json")

            mods {
                create(modId) {
                    source(sourceSets.main.get())
                }
            }
        }

        create("data") {
            workingDirectory = file("run").absolutePath

            property("forge.logging.markers", "SCAN,REGISTRIES,REGISTRYDUMP")
            property("forge.logging.console.level", "debug")

            property("mixin.debug.export", "true")
            property("mixin.env.remapRefMap", "true")
            property("mixin.env.refMapRemappingFile", "${buildDir}/createSrgToMcp/output.srg")

            args("-mixin.config=$modId.mixins.json")

            args("--mod", modId, "--all", "--output", file("src/generated/resources/"), "--existing", file("src/main/resources"))

            mods {
                create(modId) {
                    source(sourceSets.main.get())
                }
            }
        }
    }
}

sourceSets.main.get().resources {
    srcDir("src/generated/resources")
}

mixin {
    add(sourceSets.main.get(), "$modId.refmap.json")
}

dependencies {
    minecraft("net.minecraftforge:forge:$mcVersion-${property("forgeVersion")}")

    annotationProcessor("org.spongepowered:mixin:${property("mixinVersion")}:processor")
}

tasks.jar {
    manifest.attributes(
        "Specification-Title" to project.name,
        "Specification-Vendor" to "Harley O'Connor",
        "Specification-Version" to "1",
        "Implementation-Title" to project.name,
        "Implementation-Version" to project.version,
        "Implementation-Vendor" to "Harley O'Connor",
        "Implementation-Timestamp" to DateTimeFormatter.ISO_INSTANT.format(Instant.now()),
        "MixinConfigs" to "$modId.mixins.json"
    )

    archiveBaseName.set(modName)
    finalizedBy("reobfJar")
}

java {
    withSourcesJar()

    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

fun readRawChangelog(): String? {
    val versionInfoFile = file("version_info.json")
    val jsonObject = Gson().fromJson(InputStreamReader(versionInfoFile.inputStream()), JsonObject::class.java)
    return jsonObject
        ?.get(mcVersion)?.asJsonObject
        ?.get(project.version.toString())?.asString
}

val rawChangelog = readRawChangelog()!!

curseforge {
    if (!project.hasProperty("curseApiKey")) {
        project.logger.warn("API Key for CurseForge not detected; uploading will be disabled.")
        return@curseforge
    }

    apiKey = property("curseApiKey")

    project {
        id = "469647"

        addGameVersion(mcVersion)

        changelog = rawChangelog
        changelogType = "markdown"
        releaseType = optionalProperty("versionType") ?: "release"

        addArtifact(tasks.findByName("sourcesJar"))
    }
}

modrinth {
    if (!project.hasProperty("modrinthToken")) {
        project.logger.warn("Token for Modrinth not detected; uploading will be disabled.")
        return@modrinth
    }

    token.set(property("modrinthToken"))
    projectId.set(modId)
    versionNumber.set("$mcVersion-$modVersion")
    versionType.set(optionalProperty("versionType") ?: "release")
    uploadFile.set(tasks.jar.get())
    gameVersions.add(mcVersion)
    changelog.set(rawChangelog)
}

tasks.withType<GenerateModuleMetadata> {
    enabled = false
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "$modName-$mcVersion"
            version = modVersion

            from(components["java"])

            pom {
                name.set(modName)
                url.set("https://github.com/Harleyoc1/$modName")
                licenses {
                    license {
                        name.set("MIT")
                        url.set("https://mit-license.org")
                    }
                }
                developers {
                    developer {
                        id.set("Harleyoc1")
                        name.set("Harley O'Connor")
                        email.set("Harleyoc1@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/Harleyoc1/$modName.git")
                    developerConnection.set("scm:git:ssh://github.com/Harleyoc1/$modName.git")
                    url.set("https://github.com/Harleyoc1/$modName")
                }
            }

            pom.withXml {
                val element = asElement()

                // Clear dependencies.
                for (i in 0 until element.childNodes.length) {
                    val node = element.childNodes.item(i)
                    if (node?.nodeName == "dependencies") {
                        element.removeChild(node)
                    }
                }
            }
        }
    }
    repositories {
        maven("file:///${project.projectDir}/mcmodsrepo")
        if (hasProperty("harleyOConnorMavenUsername") && hasProperty("harleyOConnorMavenPassword")) {
            maven("https://harleyoconnor.com/maven") {
                name = "HarleyOConnor"
                credentials {
                    username = property("harleyOConnorMavenUsername")
                    password = property("harleyOConnorMavenPassword")
                }
            }
        } else {
            logger.log(LogLevel.WARN, "Credentials for maven not detected; it will be disabled.")
        }
    }
}

// Extensions to make CurseGradle extension slightly neater.

fun CurseExtension.project(action: CurseProject.() -> Unit) {
    this.project(closureOf(action))
}

fun CurseProject.mainArtifact(artifact: Task?, action: CurseArtifact.() -> Unit) {
    this.mainArtifact(artifact, closureOf(action))
}

fun CurseArtifact.relations(action: CurseRelation.() -> Unit) {
    this.relations(closureOf(action))
}
