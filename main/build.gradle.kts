import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20"
    id("io.micronaut.application") version "3.6.4"
}

micronaut {
    version("3.7.3")
    runtime("netty")
}

repositories {
    mavenCentral()
}

dependencies {
    runtimeOnly(project(":adapters-inbound:web"))

    runtimeOnly("ch.qos.logback:logback-classic")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        javaParameters = true
    }
}

application {
    mainClass.set("MainKt")
}
