plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    `java-library`
    `maven-publish`
}

group = "com.andrew121410.mc"
version = "1.0-SNAPSHOT"
description = "world1-6economy"
java.sourceCompatibility = JavaVersion.VERSION_17

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
    compileOnly("io.papermc.paper:paper-api:1.19.4-R0.1-SNAPSHOT")
    compileOnly("com.github.World1-6.World1-6Utils:World1-6Utils-Plugin:a018bd5be3")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}
