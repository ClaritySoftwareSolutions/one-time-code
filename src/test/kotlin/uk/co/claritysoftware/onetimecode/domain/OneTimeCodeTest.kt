package uk.co.claritysoftware.onetimecode.domain

import org.assertj.core.api.Assertions.catchThrowableOfType
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import uk.co.claritysoftware.onetimecode.domain.Status.EXPIRED
import uk.co.claritysoftware.onetimecode.domain.Status.TOO_MANY_VALIDATION_ATTEMPTS
import uk.co.claritysoftware.onetimecode.domain.assertj.assertions.assertThat
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
        assertThat(oneTimeCode)
            .isValidated()
            .hasValidationAttempts(1)
    }

    @Test
    fun `should validate given correct code on final attempt`() {
        // Given
        val oneTimeCode = aOneTimeCode(
            code = "ABCD",
            expiry = Instant.now(FIXED_CLOCK).plus(1, ChronoUnit.SECONDS),
            validationAttempts = 2 // Already had 2 failed attempts at validation, and we are about to try a 3rd time
        )

        // When
        oneTimeCode.validate("ABCD", NOW, MAX_VALIDATION_ATTEMPTS)

        // Then
        assertThat(oneTimeCode)
            .isValidated()
            .hasValidationAttempts(3)
    }

    @ParameterizedTest
    @ValueSource(ints = [1, 2, 3])
    fun `should validate given correct code on nth attempt`(attemptNumber: Int) {
        // Given
        val oneTimeCode = aOneTimeCode(
            code = "ABCD",
            expiry = Instant.now(FIXED_CLOCK).plus(1, ChronoUnit.SECONDS),
            validationAttempts = attemptNumber - 1 // A One Time Code's validationAttempts are the number of attempts that have already happened - ie. zero indexed
        )

        // When
        oneTimeCode.validate("ABCD", NOW, MAX_VALIDATION_ATTEMPTS)

        // Then
        assertThat(oneTimeCode)
            .isValidated()
            .hasValidationAttempts(attemptNumber)
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
        assertThat(oneTimeCode)
            .isNotValidated()
            .hasValidationAttempts(1)
    }

    @Test
    fun `should not validate given incorrect code on final attempt`() {
        // Given
        val oneTimeCode = aOneTimeCode(
            code = "XYZ",
            expiry = Instant.now(FIXED_CLOCK).plus(1, ChronoUnit.SECONDS),
            validationAttempts = 2
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
}
