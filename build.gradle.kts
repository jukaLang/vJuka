plugins {
    application
    kotlin("jvm") version "2.0.10"
    kotlin("plugin.serialization") version "2.0.10"
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jmonkeyengine:jme3-core:3.5.0-stable")
    implementation("org.jmonkeyengine:jme3-desktop:3.5.0-stable")
    implementation("org.jmonkeyengine:jme3-lwjgl:3.5.0-stable")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
}

application {
    mainClass = "MainKt" // Kotlin compiles the main function into MainKt class
}

