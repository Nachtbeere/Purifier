plugins {
    kotlin("jvm") version "1.3.70"
    id("com.github.johnrengelman.shadow") version "5.0.0"
}

group = "net.nachtbeere.minecraft.purifier"
version = "0.1-SNAPSHOT"

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
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("org.eclipse.jetty:jetty-jmx:9.4.28.v20200408")
    implementation("org.eclipse.jetty:jetty-util:9.4.28.v20200408")
    implementation("org.eclipse.jetty:jetty-server:9.4.28.v20200408")
    implementation("org.eclipse.jetty:jetty-servlet:9.4.28.v20200408")
    implementation("org.glassfish.jaxb:jaxb-runtime:3.0.0-M1")
    implementation("io.swagger:swagger-core:1.6.1")
    implementation("io.swagger:swagger-jaxrs:1.6.1")
    implementation("org.spigotmc:spigot-api:1.15.2-R0.1-SNAPSHOT")
    implementation("org.bukkit:bukkit:1.15.2-R0.1-SNAPSHOT")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}
