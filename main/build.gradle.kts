import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.21"
    groovy
    id("io.micronaut.application") version "3.6.3"
}

micronaut {
    version("3.7.3")
    runtime("netty")
    testRuntime("spock2")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.micronaut.flyway:micronaut-flyway")
    implementation("io.micronaut.sql:micronaut-jdbc-hikari")

    implementation(project(":adapters-inbound:web"))
    implementation(project(":adapters-outbound:persistence"))

    runtimeOnly("ch.qos.logback:logback-classic")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("io.r2dbc:r2dbc-h2")
    runtimeOnly("io.r2dbc:r2dbc-pool")

    testImplementation(project(":domain"))
    testImplementation(project(":application"))

    testImplementation("io.micronaut:micronaut-http-client")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
        javaParameters = true
    }
}

application {
    mainClass.set("buckpal.kotlin.main.MainKt")
}
