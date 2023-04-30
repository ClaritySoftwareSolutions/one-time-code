import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jlleitschuh.gradle.ktlint")
    id("org.jlleitschuh.gradle.ktlint-idea")
}

group = "uk.co.claritysoftware.onetimecode"
version = "1.0.0"

ext["assertj.version"] = "3.24.2"
ext["mockito.version"] = "4.1.0"
ext["mockito.jupiter.version"] = "5.3.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // Test dependencies
    testImplementation(kotlin("test"))
    testImplementation("org.assertj:assertj-core:${property("assertj.version")}")
    testImplementation("org.mockito.kotlin:mockito-kotlin:${property("mockito.version")}")
    testImplementation("org.mockito:mockito-junit-jupiter:${property("mockito.jupiter.version")}")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
