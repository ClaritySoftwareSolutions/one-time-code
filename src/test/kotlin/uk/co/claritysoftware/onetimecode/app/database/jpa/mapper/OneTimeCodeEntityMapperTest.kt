package uk.co.claritysoftware.onetimecode.app.database.jpa.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.mockito.kotlin.verify
import uk.co.claritysoftware.onetimecode.app.database.jpa.entity.aOneTimeCodeEntity
import uk.co.claritysoftware.onetimecode.domain.aOneTimeCode
import java.time.Instant
import java.util.UUID
import uk.co.claritysoftware.onetimecode.app.database.jpa.entity.Status as EntityStatus
import uk.co.claritysoftware.onetimecode.domain.Status as DomainStatus

@ExtendWith(MockitoExtension::class)
class OneTimeCodeEntityMapperTest {

    @InjectMocks
    private lateinit var mapper: OneTimeCodeEntityMapperImpl

    @Mock
    private lateinit var statusEntityMapper: StatusEntityMapper

    @Test
    fun `should map from domain to entity`() {
        // Given
        val code = "ABCD"
        val expiry = Instant.now()
        val validationAttempts = 4
        val status = DomainStatus.TOO_MANY_VALIDATION_ATTEMPTS

        val oneTimeCode = aOneTimeCode(
            code = code,
            expiry = expiry,
            validationAttempts = validationAttempts,
            status = status
        )

        given(statusEntityMapper.fromDomainToEntity(any())).willReturn(EntityStatus.TOO_MANY_VALIDATION_ATTEMPTS)

        val expected = aOneTimeCodeEntity(
            id = null,
            oneTimeCodeId = oneTimeCode.id,
            value = code,
            expires = expiry,
            attempts = validationAttempts,
            status = EntityStatus.TOO_MANY_VALIDATION_ATTEMPTS,
            dateCreated = null,
            dateUpdated = null,
            version = null
        )

        // When
        val actual = mapper.fromDomainToEntity(oneTimeCode)

        // Then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
        verify(statusEntityMapper).fromDomainToEntity(DomainStatus.TOO_MANY_VALIDATION_ATTEMPTS)
    }

    @Test
    fun `should map from entity to domain`() {
        // Given
        val code = "ABCD"
        val expiry = Instant.now()
        val validationAttempts = 4
        val domainId = UUID.randomUUID()

        val oneTimeCodeEntity = aOneTimeCodeEntity(
            oneTimeCodeId = domainId,
            value = code,
            expires = expiry,
            attempts = validationAttempts,
            status = EntityStatus.TOO_MANY_VALIDATION_ATTEMPTS
        )

        given(statusEntityMapper.fromEntityToDomain(any())).willReturn(DomainStatus.TOO_MANY_VALIDATION_ATTEMPTS)

        val expected = aOneTimeCode(
            id = domainId,
            code = code,
            expiry = expiry,
            validationAttempts = validationAttempts,
            status = DomainStatus.TOO_MANY_VALIDATION_ATTEMPTS
        )

        // When
        val actual = mapper.fromEntityToDomain(oneTimeCodeEntity)

        // Then
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected)
        verify(statusEntityMapper).fromEntityToDomain(EntityStatus.TOO_MANY_VALIDATION_ATTEMPTS)
    }
}
