import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.21"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":domain"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}
