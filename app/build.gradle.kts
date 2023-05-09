import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.tasks.KtLintCheckTask
import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

plugins {
    id("uk.co.claritysoftware.onetimecode.kotlin-application-conventions")

    id("org.springframework.boot") version "3.0.6"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("plugin.spring") version "1.8.20"
    kotlin("plugin.jpa") version "1.8.20"

    kotlin("kapt")
    id("org.openapi.generator") version "6.5.0"
}

apply(plugin = "org.springframework.boot")
apply(plugin = "io.spring.dependency-management")
apply(plugin = "org.jetbrains.kotlin.plugin.spring")
apply(plugin = "org.jetbrains.kotlin.plugin.allopen")
apply(plugin = "org.jetbrains.kotlin.plugin.jpa")

apply(plugin = "org.openapi.generator")

ext["mapstruct.version"] = "1.5.5.Final"
ext["springdoc.version"] = "1.6.15"
ext["mockito-kotlin.version"] = "4.1.0"
ext["h2.version"] = "2.1.214"

dependencies {
    implementation(project(":domain"))

    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    implementation("org.springdoc:springdoc-openapi-ui:${property("springdoc.version")}")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    implementation("org.mapstruct:mapstruct:${property("mapstruct.version")}")
    kapt("org.mapstruct:mapstruct-processor:${property("mapstruct.version")}")

    // Test dependencies
    testImplementation(testFixtures(project(":domain")))

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")

    testImplementation("com.h2database:h2:${property("h2.version")}")

    testImplementation("org.mockito.kotlin:mockito-kotlin:${property("mockito-kotlin.version")}")

    // Test fixtures dependencies
    testFixturesImplementation("org.assertj:assertj-core")
}

tasks.withType<KotlinCompile> {
    dependsOn(tasks.withType<GenerateTask>())
}

tasks.withType<Test> {
    dependsOn(tasks.withType<GenerateTask>())
    jvmArgs("--add-opens", "java.base/java.time=ALL-UNNAMED")
}

tasks.withType<GenerateTask> {
    enabled = false
    validateSpec.set(true)
    outputDir.set("$projectDir/build/generated")
    generatorName.set("kotlin-spring")
    generateModelTests.set(false)
    generateModelDocumentation.set(false)
    globalProperties.set(
        mapOf(
            "apis" to "false",
            "invokers" to "false",
            "models" to ""
        )
    )
    configOptions.set(
        mapOf(
            "dateLibrary" to "java8",
            "serializationLibrary" to "jackson",
            "enumPropertyNaming" to "UPPERCASE",
            "useBeanValidation" to "true",
            "useSpringBoot3" to "true"
        )
    )
}

tasks.create("generate-models-from-openapi-document-VoterCardApplicationAPIs.yaml", GenerateTask::class) {
    enabled = true
    inputSpec.set("$projectDir/src/main/resources/openapi/OneTimeCode.yml")
    packageName.set("uk.co.claritysoftware.onetimecode")
}

// Add the generated code to the source sets
sourceSets["main"].java {
    this.srcDir("$projectDir/build/generated")
}

// Linting is dependent on GenerateTask
tasks.withType<KtLintCheckTask> {
    dependsOn(tasks.withType<GenerateTask>())
}

// Exclude generated code from linting
ktlint {
    filter {
        exclude { projectDir.toURI().relativize(it.file.toURI()).path.contains("/generated/") }
    }
}

kapt {
    arguments {
        arg("mapstruct.defaultComponentModel", "spring")
        arg("mapstruct.unmappedTargetPolicy", "IGNORE")
    }
}
