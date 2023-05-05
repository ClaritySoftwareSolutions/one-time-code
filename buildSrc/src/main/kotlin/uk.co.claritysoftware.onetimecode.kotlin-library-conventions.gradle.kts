plugins {
    id("uk.co.claritysoftware.onetimecode.kotlin-common-conventions")

    // Apply the java-library plugin for API and implementation separation.
    `java-library`
    // Apply the java-test-fixtures plugin to allow test classes to be imported by other modules.
    `java-test-fixtures`
}
