package uk.co.claritysoftware.onetimecode.app

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@Import(TestConfig::class)
abstract class IntegrationTest {

    @Autowired
    protected lateinit var fixedClock: Clock
}

@TestConfiguration
class TestConfig {

    @Bean
    fun fixedClock(): Clock = Clock.fixed(Instant.now(), ZoneOffset.UTC)
}
