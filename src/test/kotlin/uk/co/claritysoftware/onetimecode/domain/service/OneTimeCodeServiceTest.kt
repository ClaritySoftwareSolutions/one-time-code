package uk.co.claritysoftware.onetimecode.domain.service

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowableOfType
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.BDDMockito.given
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import uk.co.claritysoftware.onetimecode.domain.OneTimeCodeNotFoundException
import uk.co.claritysoftware.onetimecode.domain.OneTimeCodeTooManyAttemptsException
import uk.co.claritysoftware.onetimecode.domain.OneTimeCodeValidationNotMatchedException
import uk.co.claritysoftware.onetimecode.domain.aOneTimeCode
import uk.co.claritysoftware.onetimecode.domain.assertj.assertions.assertThat
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import java.util.UUID

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OneTimeCodeServiceTest {

    private lateinit var service: OneTimeCodeService

    private val configuration = mock<OneTimeCodeServiceConfiguration>()

    private val persistenceService = mock<OneTimeCodePersistenceService>()

    private val fixedClock = Clock.fixed(Instant.now(), ZoneOffset.UTC)

    @BeforeAll
    fun setup() {
        service = OneTimeCodeService(
            configuration,
            persistenceService,
            fixedClock
        )
    }

    @Test
    fun `should create one time code`() {
        // Given
        val characterSet = setOf('A')
        val codeLength = 4
        val codeTtlSeconds = 120L

        given(configuration.characterSet).willReturn(characterSet)
        given(configuration.codeLength).willReturn(codeLength)
        given(configuration.codeTtlSeconds).willReturn(codeTtlSeconds)

        val expectedCode = "AAAA"
        val expectedExpiry = Instant.now(fixedClock).plusSeconds(codeTtlSeconds)
        val expectedValidationAttempts = 0

        // When
        val actual = service.createOneTimeCode()

        // Then
        assertThat(actual)
            .hasCode(expectedCode)
            .hasValidationAttempts(expectedValidationAttempts)
            .hasExpiry(expectedExpiry)
        verify(persistenceService).save(actual)
    }

    @Nested
    inner class ValidateOneTimeCode {

        @Test
        fun `should not validate given one time code not found`() {
            // Given
            val code = "ABCD"
            val oneTimeCodeId = UUID.randomUUID()

            given(persistenceService.findByDomainId(any())).willReturn(null)

            // When
            val exception = catchThrowableOfType(
                { service.validateOneTimeCode(oneTimeCodeId, code) },
                OneTimeCodeNotFoundException::class.java
            )

            // Then
            assertThat(exception.domainId).isEqualTo(oneTimeCodeId)
            verify(persistenceService).findByDomainId(oneTimeCodeId)
        }

        @Test
        fun `should validate and save one time code given validation is successful`() {
            // Given
            val oneTimeCode = aOneTimeCode(
                expiry = Instant.now(fixedClock).plus(1, ChronoUnit.SECONDS)
            )
            val code = oneTimeCode.code
            val oneTimeCodeId = oneTimeCode.id

            given(configuration.maximumValidationAttempts).willReturn(3)

            given(persistenceService.findByDomainId(any())).willReturn(oneTimeCode)

            // When
            service.validateOneTimeCode(oneTimeCodeId, code)

            // Then
            verify(persistenceService).findByDomainId(oneTimeCodeId)
            verify(persistenceService).save(oneTimeCode)
        }

        @Test
        fun `should validate and save one time code given validation is unsuccessful`() {
            // Given
            val oneTimeCode = aOneTimeCode(
                code = "ABCD",
                expiry = Instant.now(fixedClock).plus(1, ChronoUnit.SECONDS)
            )
            val oneTimeCodeId = oneTimeCode.id

            given(configuration.maximumValidationAttempts).willReturn(3)

            given(persistenceService.findByDomainId(any())).willReturn(oneTimeCode)

            // When
            val exception = catchThrowableOfType(
                { service.validateOneTimeCode(oneTimeCodeId, "WXYZ") },
                OneTimeCodeValidationNotMatchedException::class.java
            )

            // Then
            assertThat(exception.oneTimeCode).isEqualTo(oneTimeCode)
            verify(persistenceService).findByDomainId(oneTimeCodeId)
            verify(persistenceService).save(oneTimeCode)
        }

        @Test
        fun `should validate and delete one time code given unsuccessful validation exceeds number of attempts`() {
            // Given
            val oneTimeCode = aOneTimeCode(
                code = "ABCD",
                expiry = Instant.now(fixedClock).plus(1, ChronoUnit.SECONDS),
                validationAttempts = 2
            )
            val oneTimeCodeId = oneTimeCode.id

            given(configuration.maximumValidationAttempts).willReturn(3)

            given(persistenceService.findByDomainId(any())).willReturn(oneTimeCode)

            // When
            val exception = catchThrowableOfType(
                { service.validateOneTimeCode(oneTimeCodeId, "WXYZ") },
                OneTimeCodeTooManyAttemptsException::class.java
            )

            // Then
            assertThat(exception.oneTimeCode).isEqualTo(oneTimeCode)
            verify(persistenceService).findByDomainId(oneTimeCodeId)
            verify(persistenceService).remove(oneTimeCode)
        }
    }
}
