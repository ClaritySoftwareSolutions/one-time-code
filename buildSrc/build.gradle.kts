import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

extra["kotlin.plugin.version"] = "1.8.20"
extra["ktlint.plugin.version"] = "11.3.2"

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${property("kotlin.plugin.version")}")
    implementation("org.jlleitschuh.gradle:ktlint-gradle:${property("ktlint.plugin.version")}")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "170"
    }
}
