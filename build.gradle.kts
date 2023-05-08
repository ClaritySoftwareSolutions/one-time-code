plugins {
	id("org.jlleitschuh.gradle.ktlint") version "10.3.0"
	id("org.jlleitschuh.gradle.ktlint-idea") version "10.3.0"
}

apply(plugin = "org.jlleitschuh.gradle.ktlint")

repositories {
	gradlePluginPortal()
}
