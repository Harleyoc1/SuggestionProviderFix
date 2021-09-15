plugins {
    `kotlin-dsl`
    `java-library`
}

repositories {
    mavenCentral()
    maven("https://maven.minecraftforge.net")
    maven("https://maven.parchmentmc.org")
    maven("https://repo.spongepowered.org/repository/maven-public/")
}

dependencies {
    implementation("net.minecraftforge.gradle:ForgeGradle:5.1.+")
    implementation("org.parchmentmc:librarian:1.+")
    implementation("org.spongepowered:mixingradle:0.7-SNAPSHOT")
    implementation("com.google.code.gson:gson:2.8.7")
}
