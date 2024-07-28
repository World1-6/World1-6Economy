plugins {
//    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.github.goooler.shadow") version "8.1.7" // https://github.com/johnrengelman/shadow/pull/876 https://github.com/Goooler/shadow https://plugins.gradle.org/plugin/io.github.goooler.shadow
    `java-library`
    `maven-publish`
}

group = "com.andrew121410.mc"
version = "1.0-SNAPSHOT"
description = "world1-6economy"
java.sourceCompatibility = JavaVersion.VERSION_21

tasks {
    build {
        dependsOn("shadowJar")
    }

    compileJava {
        options.encoding = "UTF-8"
    }

    jar {
        enabled = false
    }

    shadowJar {
        archiveBaseName.set("World1-6Economy")
        archiveClassifier.set("")
        archiveVersion.set("")

//        relocate("org.bstats", "com.andrew121410.mc.world16essentials.bstats")
    }
}

repositories {
    mavenCentral()
    mavenLocal()

    maven {
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }

    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }

    maven {
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }

    maven {
        url = uri("https://repo.opencollab.dev/maven-releases/")
    }

    maven {
        url = uri("https://repo.opencollab.dev/maven-snapshots/")
    }

    maven {
        url = uri("https://jitpack.io")
    }
}


dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21-R0.1-SNAPSHOT")
    compileOnly("com.github.World1-6.World1-6Utils:World1-6Utils-Plugin:634da50bcc")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}
