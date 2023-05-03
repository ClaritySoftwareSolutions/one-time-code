package uk.co.claritysoftware.onetimecode

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class OneTimeCodeApplication

fun main(args: Array<String>) {
    runApplication<OneTimeCodeApplication>(*args)
}
