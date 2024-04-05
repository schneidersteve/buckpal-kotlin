import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.23"
    id("com.google.devtools.ksp") version "1.9.23-1.0.19"
    id("groovy")
    id("io.micronaut.application") version "4.3.5"
}

micronaut {
    runtime("netty")
    testRuntime("spock2")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.micronaut.flyway:micronaut-flyway")
    implementation("io.micronaut.sql:micronaut-jdbc-hikari")

    implementation(project(":adapters-inbound:rest"))
    implementation(project(":adapters-outbound:persistence"))

    runtimeOnly("ch.qos.logback:logback-classic")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("io.r2dbc:r2dbc-h2")
    runtimeOnly("io.r2dbc:r2dbc-pool")
    runtimeOnly("org.yaml:snakeyaml")
    runtimeOnly("io.micronaut.reactor:micronaut-reactor")
    runtimeOnly("io.micronaut.kotlin:micronaut-kotlin-runtime")
    runtimeOnly("org.jetbrains.kotlin:kotlin-reflect:1.9.23")
    runtimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.8.0")

    testImplementation(project(":domain"))
    testImplementation(project(":application"))

    testImplementation("io.micronaut:micronaut-http-client")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
    kotlinOptions.javaParameters = true
}

application {
    mainClass.set("buckpal.kotlin.main.MainKt")
}
