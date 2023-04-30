package uk.co.claritysoftware.onetimecode.domain

import org.assertj.core.api.Assertions.catchThrowableOfType
import org.junit.jupiter.api.Test
import uk.co.claritysoftware.onetimecode.domain.Status.EXPIRED
import uk.co.claritysoftware.onetimecode.domain.Status.TOO_MANY_VALIDATION_ATTEMPTS
import uk.co.claritysoftware.onetimecode.domain.testsupport.assertj.assertions.assertThat
import uk.co.claritysoftware.onetimecode.domain.testsupport.testdata.aOneTimeCode
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

class OneTimeCodeTest {

    companion object {
        private const val MAX_VALIDATION_ATTEMPTS = 3
        private val FIXED_CLOCK = Clock.fixed(Instant.now(), ZoneOffset.UTC)
        private val NOW = Instant.now(FIXED_CLOCK)
    }

    @Test
    fun `should validate given correct code`() {
        // Given
        val oneTimeCode = aOneTimeCode(
            code = "ABCD",
            expiry = Instant.now(FIXED_CLOCK).plus(1, ChronoUnit.SECONDS)
        )

        // When
        oneTimeCode.validate("ABCD", NOW, MAX_VALIDATION_ATTEMPTS)

        // Then
        assertThat(oneTimeCode).isValidated()
    }

    @Test
    fun `should not validate given one time code has expired`() {
        // Given
        val oneTimeCode = aOneTimeCode(
            expiry = Instant.now(FIXED_CLOCK).minus(1, ChronoUnit.SECONDS)
        )

        // When
        val exception = catchThrowableOfType(
            { oneTimeCode.validate("ABCD", NOW, MAX_VALIDATION_ATTEMPTS) },
            OneTimeCodeExpiredException::class.java
        )

        // Then
        assertThat(exception.oneTimeCode).isEqualTo(oneTimeCode)
        assertThat(oneTimeCode).hasStatus(EXPIRED)
    }

    @Test
    fun `should not validate given max validation attempts have been exceeded`() {
        // Given
        val oneTimeCode = aOneTimeCode(
            expiry = Instant.now(FIXED_CLOCK).plus(1, ChronoUnit.SECONDS),
            validationAttempts = 4
        )

        // When
        val exception = catchThrowableOfType(
            { oneTimeCode.validate("ABCD", NOW, MAX_VALIDATION_ATTEMPTS) },
            OneTimeCodeTooManyAttemptsException::class.java
        )

        // Then
        assertThat(exception.oneTimeCode).isEqualTo(oneTimeCode)
        assertThat(oneTimeCode).hasStatus(TOO_MANY_VALIDATION_ATTEMPTS)
    }

    @Test
    fun `should not validate given incorrect code`() {
        // Given
        val oneTimeCode = aOneTimeCode(
            code = "XYZ",
            expiry = Instant.now(FIXED_CLOCK).plus(1, ChronoUnit.SECONDS)
        )

        // When
        val exception = catchThrowableOfType(
            { oneTimeCode.validate("ABCD", NOW, MAX_VALIDATION_ATTEMPTS) },
            OneTimeCodeValidationNotMatchedException::class.java
        )

        // Then
        assertThat(exception.oneTimeCode).isEqualTo(oneTimeCode)
        assertThat(oneTimeCode).isNotValidated()
    }
}
