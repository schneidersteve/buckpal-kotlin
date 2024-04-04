import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.23"
    id("com.google.devtools.ksp") version "1.9.23-1.0.19"
    id("groovy")
    id("io.micronaut.library") version "4.3.5"
}

micronaut {
    testRuntime("spock2")
}

repositories {
    mavenCentral()
}

dependencies {
    ksp("io.micronaut.data:micronaut-data-processor")

    implementation(project(":domain"))
    implementation(project(":application"))

    implementation("io.micronaut.data:micronaut-data-r2dbc")
    implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")

    runtimeOnly("ch.qos.logback:logback-classic")
//    runtimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.8.0")

//    testImplementation(project(":testdata"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
    kotlinOptions.javaParameters = true
}

//tasks.withType<GroovyCompile> {
//    classpath += files(compileKotlin)
//}
