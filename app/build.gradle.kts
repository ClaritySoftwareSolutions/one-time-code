import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.tasks.KtLintCheckTask
import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

plugins {
    id("uk.co.claritysoftware.onetimecode.kotlin-application-conventions")

    id("org.springframework.boot") version "3.0.6"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("plugin.spring") version "1.8.20"

    kotlin("kapt")
    id("org.openapi.generator") version "6.5.0"
}

apply(plugin = "org.springframework.boot")
apply(plugin = "io.spring.dependency-management")
apply(plugin = "org.jetbrains.kotlin.plugin.spring")
apply(plugin = "org.jetbrains.kotlin.plugin.allopen")

apply(plugin = "org.openapi.generator")

dependencies {
    implementation(project(":domain"))

    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    implementation("org.springdoc:springdoc-openapi-ui:1.6.15")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    implementation("org.mapstruct:mapstruct:1.5.5.Final")
    kapt("org.mapstruct:mapstruct-processor:1.5.5.Final")

    // Test dependencies
    testImplementation(testFixtures(project(":app")))
    testImplementation(testFixtures(project(":domain")))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")

    testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")

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
