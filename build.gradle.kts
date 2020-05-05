import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.3.71"
    id("com.github.johnrengelman.shadow") version "5.0.0"
}

group = "net.nachtbeere.minecraft.purifier"
version = "0.10.1-SNAPSHOT"

repositories {
    mavenLocal()
    jcenter()
    maven { url = uri("https://papermc.io/repo/repository/maven-public/") }
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots") }
    maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/public/") }
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("io.javalin:javalin:3.8.0")
    implementation("io.swagger.core.v3:swagger-core:2.1.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.10.3")
    implementation("cc.vileda:kotlin-openapi3-dsl:0.20.2")
    implementation("com.auth0:java-jwt:3.10.3")
    implementation("com.destroystokyo.paper:paper-api:1.15.2-R0.1-SNAPSHOT")
    testImplementation("com.github.seeseemelk:MockBukkit:v1.15-SNAPSHOT")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}
