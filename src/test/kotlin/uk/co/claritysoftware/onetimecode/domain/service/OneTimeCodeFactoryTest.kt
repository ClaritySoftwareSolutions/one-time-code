package uk.co.claritysoftware.onetimecode.domain.service

import org.junit.jupiter.api.Test
import uk.co.claritysoftware.onetimecode.domain.assertj.assertions.assertThat
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

class OneTimeCodeFactoryTest {

    private val fixedClock = Clock.fixed(Instant.EPOCH, ZoneOffset.UTC)
    private val factory = OneTimeCodeFactory(fixedClock)

    @Test
    fun `should create one time code`() {
        // Given
        val characterSet = setOf('A', 'B', 'C', 'D')
        val codeLength = 6
        val codeTtlSeconds = 60L

        val expectedExpiry = Instant.now(fixedClock).plusSeconds(codeTtlSeconds)

        // When
        val actual = factory.createOneTimeCode(characterSet, codeLength, codeTtlSeconds)

        // Then
        assertThat(actual)
            .hasCodeLength(6)
            .codeContainsOnlyCharactersFrom(characterSet)
            .hasValidationAttempts(0)
            .hasExpiry(expectedExpiry)
            .isNotValidated()
    }
}
