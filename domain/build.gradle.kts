plugins {
    id("uk.co.claritysoftware.onetimecode.kotlin-library-conventions")
}

ext["assertj.version"] = "3.24.2"
ext["mockito.version"] = "4.1.0"
ext["mockito.jupiter.version"] = "5.3.1"

dependencies {
    // Test dependencies
    testImplementation(kotlin("test"))
    testImplementation("org.assertj:assertj-core:${property("assertj.version")}")
    testImplementation("org.mockito.kotlin:mockito-kotlin:${property("mockito.version")}")
    testImplementation("org.mockito:mockito-junit-jupiter:${property("mockito.jupiter.version")}")
}
