plugins {
    `kotlin-dsl`
    `java-library`
}

repositories {
    mavenCentral()
    maven("https://files.minecraftforge.net/maven")
    maven("https://repo.spongepowered.org/repository/maven-public/")
}

dependencies {
    implementation("net.minecraftforge.gradle:ForgeGradle:4.1.+")
    implementation("org.spongepowered:mixingradle:0.7-SNAPSHOT")
    implementation("com.google.code.gson:gson:2.8.7")
}
