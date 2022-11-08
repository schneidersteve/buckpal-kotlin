import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.20"
    groovy
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":domain"))

    implementation("javax.transaction:javax.transaction-api:1.3")

    testImplementation(platform("org.apache.groovy:groovy-bom:4.0.6"))
    testImplementation("org.apache.groovy:groovy")
    testImplementation(platform("org.spockframework:spock-bom:2.3-groovy-4.0"))
    testImplementation("org.spockframework:spock-core")
    testRuntimeOnly("net.bytebuddy:byte-buddy:1.12.18")
    testRuntimeOnly("org.objenesis:objenesis:3.3")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
