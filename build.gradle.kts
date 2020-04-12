import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

plugins {
    application
    kotlin("jvm") version "1.3.70"
    id("com.github.johnrengelman.shadow") version "5.0.0"
}

group = "net.nachtbeere.minecraft.purifier"
version = "0.1-SNAPSHOT"

application {
    mainClassName = "net.nachtbeere.minecraft.purifier.Purifier"
}

tasks.withType<Jar> {
    manifest {
        attributes(
                mapOf(
                        "Main-Class" to application.mainClassName
                )
        )
    }
}

repositories {
    mavenLocal()
    jcenter()
    maven {
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
        content {
            includeGroup("org.bukkit")
            includeGroup("org.spigotmc")
        }
    }
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("org.eclipse.jetty:jetty-server:9.4.28.v20200408")
    implementation("org.eclipse.jetty:jetty-servlet:9.4.28.v20200408")
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("org.spigotmc:spigot-api:1.15.2-R0.1-SNAPSHOT")
    implementation("org.bukkit:bukkit:1.15.2-R0.1-SNAPSHOT")
}

kotlin.sourceSets["main"].kotlin.srcDirs("src")
kotlin.sourceSets["test"].kotlin.srcDirs("test")

sourceSets["main"].resources.srcDirs("resources")
sourceSets["test"].resources.srcDirs("testresources")
