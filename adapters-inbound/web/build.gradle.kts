import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.21"
    groovy
    kotlin("kapt") version "1.7.21"
    id("io.micronaut.library") version "3.6.4"
}

micronaut {
    version("3.7.3")
    testRuntime("spock2")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":application"))

    implementation("io.micronaut:micronaut-http-server-netty")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

    testImplementation("org.spockframework:spock-core")
    testImplementation("io.micronaut:micronaut-http-client")

    runtimeOnly("ch.qos.logback:logback-classic")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
        javaParameters = true
    }
}
