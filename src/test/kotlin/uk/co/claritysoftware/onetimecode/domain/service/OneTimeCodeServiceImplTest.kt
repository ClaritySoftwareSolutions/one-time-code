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
import uk.co.claritysoftware.onetimecode.domain.OneTimeCodeValidationNotMatchedException
import uk.co.claritysoftware.onetimecode.domain.aOneTimeCode
import uk.co.claritysoftware.onetimecode.domain.repository.OneTimeCodeRepository
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import java.util.UUID

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OneTimeCodeServiceImplTest {

    private lateinit var service: OneTimeCodeServiceImpl

    private val factory = mock<OneTimeCodeFactory>()

    private val configuration = mock<OneTimeCodeServiceConfiguration>()

    private val repository = mock<OneTimeCodeRepository>()

    private val fixedClock = Clock.fixed(Instant.now(), ZoneOffset.UTC)

    @BeforeAll
    fun setup() {
        service = OneTimeCodeServiceImpl(
            factory,
            configuration,
            repository,
            fixedClock
        )
    }

    @Test
    fun `should create one time code`() {
        // Given
        val characterSet = setOf('A', 'B', 'C')
        val codeLength = 4
        val codeTtlSeconds = 120L

        given(configuration.characterSet).willReturn(characterSet)
        given(configuration.codeLength).willReturn(codeLength)
        given(configuration.codeTtlSeconds).willReturn(codeTtlSeconds)

        val expectedCode = "ABCA"
        val expectedExpiry = Instant.now().plusSeconds(codeTtlSeconds)
        val expectedValidationAttempts = 0
        val expectedOneTimeCode = aOneTimeCode(
            code = expectedCode,
            expiry = expectedExpiry,
            validationAttempts = expectedValidationAttempts
        )
        given(factory.createOneTimeCode(any(), any(), any())).willReturn(expectedOneTimeCode)

        // When
        val actual = service.createOneTimeCode()

        // Then
        assertThat(actual).isEqualTo(expectedOneTimeCode)
        verify(repository).save(expectedOneTimeCode)
        verify(factory).createOneTimeCode(characterSet, codeLength, codeTtlSeconds)
    }

    @Nested
    inner class ValidateOneTimeCode {

        @Test
        fun `should not validate given one time code not found`() {
            // Given
            val code = "ABCD"
            val oneTimeCodeId = UUID.randomUUID()

            given(repository.findByDomainId(any())).willReturn(null)

            // When
            val exception = catchThrowableOfType(
                { service.validateOneTimeCode(oneTimeCodeId, code) },
                OneTimeCodeNotFoundException::class.java
            )

            // Then
            assertThat(exception.domainId).isEqualTo(oneTimeCodeId)
            verify(repository).findByDomainId(oneTimeCodeId)
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

            given(repository.findByDomainId(any())).willReturn(oneTimeCode)

            // When
            service.validateOneTimeCode(oneTimeCodeId, code)

            // Then
            verify(repository).findByDomainId(oneTimeCodeId)
            verify(repository).save(oneTimeCode)
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

            given(repository.findByDomainId(any())).willReturn(oneTimeCode)

            // When
            val exception = catchThrowableOfType(
                { service.validateOneTimeCode(oneTimeCodeId, "WXYZ") },
                OneTimeCodeValidationNotMatchedException::class.java
            )

            // Then
            assertThat(exception.oneTimeCode).isEqualTo(oneTimeCode)
            verify(repository).findByDomainId(oneTimeCodeId)
            verify(repository).save(oneTimeCode)
        }
    }
}
