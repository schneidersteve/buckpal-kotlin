import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20"
    groovy
    kotlin("kapt") version "1.7.20"
    id("io.micronaut.library") version "3.6.4"
}

micronaut {
    version("3.7.3")
}

repositories {
    mavenCentral()
}

dependencies {
    kapt("io.micronaut:micronaut-inject-java")

    implementation(project(":domain"))
    implementation(project(":common"))

    implementation("javax.transaction:javax.transaction-api:1.3")

    testImplementation("org.spockframework:spock-core")
    testRuntimeOnly("net.bytebuddy:byte-buddy:1.12.18")
    testRuntimeOnly("org.objenesis:objenesis:3.3")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        javaParameters = true
    }
}
