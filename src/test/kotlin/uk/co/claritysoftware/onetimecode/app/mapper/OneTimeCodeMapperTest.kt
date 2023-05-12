package uk.co.claritysoftware.onetimecode.app.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.BDDMockito.verify
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import uk.co.claritysoftware.onetimecode.app.aOneTimeCodeResponse
import uk.co.claritysoftware.onetimecode.domain.aOneTimeCode
import java.time.Clock
import java.time.Instant
import java.time.OffsetDateTime
import java.time.ZoneOffset

@ExtendWith(MockitoExtension::class)
class OneTimeCodeMapperTest {

    @InjectMocks
    private lateinit var oneTimeCodeMapper: OneTimeCodeMapperImpl

    @Mock
    private lateinit var instantMapper: InstantMapper

    private val fixedClock = Clock.fixed(Instant.now(), ZoneOffset.UTC)

    @Test
    fun `should map from domain to api`() {
        // Given
        val code = "ABCD"
        val expiryInstant = Instant.now(fixedClock)

        val domainObject = aOneTimeCode(
            code = code,
            expiry = expiryInstant
        )

        val expiryOffsetDateTime = OffsetDateTime.now(fixedClock)
        given(instantMapper.toOffsetDateTime(any())).willReturn(expiryOffsetDateTime)

        val expected = aOneTimeCodeResponse(
            id = domainObject.id,
            code = code,
            expiry = expiryOffsetDateTime
        )

        // When
        val actual = oneTimeCodeMapper.fromDomainToApi(domainObject)

        // Then
        assertThat(actual).isEqualTo(expected)
        verify(instantMapper).toOffsetDateTime(expiryInstant)
    }
}
