package com.perasite.spikotgradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.jvm.tasks.Jar
import org.gradle.kotlin.dsl.add
import org.gradle.kotlin.dsl.exclude
import org.gradle.kotlin.dsl.maven
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val excludeSet = listOf(
    "org.jetbrains.kotlin" to "kotlin-stdlib",
    "org.jetbrains.kotlin" to "kotlin-stdlib-common",
    "org.jetbrains.kotlin" to "kotlin-stdlib-jdk7",
    "org.jetbrains.kotlin" to "kotlin-stdlib-jdk8",
    "org.jetbrains.kotlin" to "kotlin-reflect",
    "org.jetbrains.kotlinx" to "kotlinx-coroutines-core",
    "org.jetbrains.kotlinx" to "kotlinx-coroutines-core-common",
    "org.jetbrains.kotlinx" to "kotlinx-coroutines-jdk8",
    "org.jetbrains.kotlinx" to "kotlinx-serialization-runtime",
    "org.jetbrains.kotlinx" to "kotlinx-serialization-runtime-common",
    "io.github.microutils" to "kotlin-logging",
    "com.esotericsoftware.yamlbeans" to "yamlbeans",
    "net.swiftzer.semver" to "semver",
    "com.github.salomonbrys.kotson" to "kotson"
)

@Suppress("UnstableApiUsage")
class SpikotGradlePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.buildscript.repositories.apply {
            mavenCentral()
            maven("https://repo.heartpattern.io/repository/maven-public/")
        }
        project.repositories.apply {
            maven("https://repo.heartpattern.io/repository/maven-public/")
            maven("https://jitpack.io")
        }
        
        project.plugins.apply("org.jetbrains.kotlin.jvm")
        project.plugins.apply("org.jetbrains.kotlin.kapt")
        project.plugins.apply("org.jetbrains.kotlin.plugin.serialization")
        
        with(project.dependencies) {
            add("compileOnly", "org.spigotmc:plugin-annotations:1.2.2-SNAPSHOT") {
                exclude("org.bukkit", "bukkit")
            }
            add("kapt", "org.spigotmc:plugin-annotations:1.2.2-SNAPSHOT")
        }
        
        project.tasks.withType(KotlinCompile::class.java) { config ->
            config.kotlinOptions {
                jvmTarget = "1.8"
                freeCompilerArgs = freeCompilerArgs + listOf(
                    "-XXLanguage:+InlineClasses",
                    "-Xuse-experimental=kotlin.Experimental"
                )
            }
        }
        
        project.afterEvaluate { prj ->
            prj.dependencies.add("kapt", "com.github.PeraSite:SpikotClassLocator:-SNAPSHOT")
            val impl = project.configurations.getByName("implementation")
            val shade = project.configurations.create("shade")
            shade.extendsFrom(impl)
            
            for ((group, module) in excludeSet)
                shade.exclude(group, module)
            
            prj.tasks.create("createPlugin", Jar::class.java) { task ->
                task.archiveFileName.set("${prj.name}-Plugin.jar")
                task.from(shade.map {
                    if (it.isDirectory)
                        it
                    else
                        prj.zipTree(it)
                })
                task.with(prj.tasks.getByName("jar") as Jar)
            }
        }
    }
}
