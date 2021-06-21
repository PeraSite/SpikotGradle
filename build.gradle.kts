plugins {
    kotlin("jvm") version "1.5.10"
    kotlin("plugin.serialization") version "1.5.10"
    kotlin("kapt") version "1.5.10"
    
    id("java-gradle-plugin")
    id("maven-publish")
    id("com.gradle.plugin-publish") version "0.15.0"
}

group = "com.perasite"
version = "5.0.0"

repositories {
    mavenCentral()
}

val kotlinVersion = "1.5.10"

dependencies {
    implementation(kotlin("stdlib", kotlinVersion))
    implementation(gradleKotlinDsl())
    implementation(kotlin("gradle-plugin", kotlinVersion))
    
    runtimeOnly(kotlin("annotation-processing-gradle", kotlinVersion))
    runtimeOnly(kotlin("serialization", kotlinVersion))
}

gradlePlugin {
    plugins {
        create("SpikotGradle") {
            id = "com.perasite.spikotgradle"
            displayName = "SpikotGradle"
            description = "Forked gradle plugin for Spikot Framework"
            implementationClass = "com.perasite.spikotgradle.SpikotGradlePlugin"
        }
    }
}

pluginBundle {
    website = "https://github.com/PeraSite/SpikotGradle"
    vcsUrl = "https://github.com/PeraSite/SpikotGradle"
    tags = listOf("bukkit", "spikot")
}

publishing {
    repositories {
        mavenLocal()
    }
}
