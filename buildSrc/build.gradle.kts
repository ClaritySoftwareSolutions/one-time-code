import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
}

extra["kotlin.plugin.version"] = "1.8.20"

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${property("kotlin.plugin.version")}")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17"
    }
}
