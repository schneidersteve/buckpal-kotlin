import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.21"
    groovy
    kotlin("kapt") version "1.7.21"
    id("io.micronaut.library") version "3.6.3"
}

micronaut {
    version("3.7.3")
    testRuntime("spock2")
}

repositories {
    mavenCentral()
}

dependencies {
    kapt("io.micronaut:micronaut-inject-java")
    kapt("io.micronaut.data:micronaut-data-processor")

    implementation(project(":domain"))
    implementation(project(":application"))

    implementation("io.micronaut.data:micronaut-data-r2dbc")
    implementation("jakarta.persistence:jakarta.persistence-api:3.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

    runtimeOnly("ch.qos.logback:logback-classic")
    runtimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-reactive:1.6.4")

    testImplementation(project(":testdata"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
        javaParameters = true
    }
}

//tasks.withType<GroovyCompile> {
//    classpath += files(compileKotlin)
//}
