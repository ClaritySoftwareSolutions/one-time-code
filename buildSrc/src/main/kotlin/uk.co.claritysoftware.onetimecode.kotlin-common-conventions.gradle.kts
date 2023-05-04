import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jlleitschuh.gradle.ktlint")
    id("org.jlleitschuh.gradle.ktlint-idea")
}

apply(plugin = "org.jetbrains.kotlin.jvm")
apply(plugin = "org.jlleitschuh.gradle.ktlint")

group = "uk.co.claritysoftware.onetimecode"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.2")
}

java.sourceCompatibility = JavaVersion.VERSION_17
tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
