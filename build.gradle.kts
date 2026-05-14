plugins {
    id("com.gradleup.shadow") version "9.4.1" // https://github.com/GradleUp/shadow
    `java-library`
    `maven-publish`
}

group = "com.andrew121410.mc"
version = "1.0.0"
description = "world1-6economy"
java.targetCompatibility = JavaVersion.VERSION_25
java.sourceCompatibility = JavaVersion.VERSION_25

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
    }
}

repositories {
    mavenLocal()
    mavenCentral()

    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }

    maven {
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
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
    compileOnly("io.papermc.paper:paper-api:26.1.2.build.63-stable")
    compileOnly("com.github.World1-6.World1-6Utils:World1-6Utils-Plugin:ab7a07dd78")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1") {
        exclude(group = "org.bukkit", module = "bukkit")
    }
}

publishing {
    publications {
        create<MavenPublication>("shadow") {
            artifact(tasks.named("shadowJar"))
        }
    }
}
