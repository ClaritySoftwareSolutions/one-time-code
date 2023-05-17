package uk.co.claritysoftware.onetimecode.app.database.jpa

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import uk.co.claritysoftware.onetimecode.app.database.jpa.entity.OneTimeCodeEntity
import uk.co.claritysoftware.onetimecode.app.database.jpa.entity.Status
import uk.co.claritysoftware.onetimecode.app.database.jpa.entity.aOneTimeCodeEntity
import uk.co.claritysoftware.onetimecode.app.database.jpa.mapper.OneTimeCodeEntityMapper
import uk.co.claritysoftware.onetimecode.app.database.jpa.mapper.StatusEntityMapper
import uk.co.claritysoftware.onetimecode.app.database.jpa.repository.OneTimeCodeRepository
import uk.co.claritysoftware.onetimecode.domain.aOneTimeCode
import java.util.UUID
import uk.co.claritysoftware.onetimecode.domain.Status as DomainStatus

@ExtendWith(MockitoExtension::class)
class JpaBackedOneTimeCodePersistenceServiceTest {

    @InjectMocks
    private lateinit var persistenceService: JpaBackedOneTimeCodePersistenceService

    @Mock
    private lateinit var oneTimeCodeRepository: OneTimeCodeRepository

    @Mock
    private lateinit var oneTimeCodeEntityMapper: OneTimeCodeEntityMapper

    @Mock
    private lateinit var statusEntityMapper: StatusEntityMapper

    @Test
    fun `should find by domain ID given entity exists in repository`() {
        // Given
        val domainId = UUID.randomUUID()

        val oneTimeCodeEntity = aOneTimeCodeEntity(
            oneTimeCodeId = domainId
        )
        given(oneTimeCodeRepository.findByOneTimeCodeId(any())).willReturn(oneTimeCodeEntity)

        val expectedOneTimeCode = aOneTimeCode(
            id = domainId
        )
        given(oneTimeCodeEntityMapper.fromEntityToDomain(any())).willReturn(expectedOneTimeCode)

        // When
        val actual = persistenceService.findByDomainId(domainId)

        // Then
        assertThat(actual).isEqualTo(expectedOneTimeCode)
        verify(oneTimeCodeRepository).findByOneTimeCodeId(domainId)
        verify(oneTimeCodeEntityMapper).fromEntityToDomain(oneTimeCodeEntity)
    }

    @Test
    fun `should not find by domain ID given entity does not exist in repository`() {
        // Given
        val domainId = UUID.randomUUID()

        given(oneTimeCodeRepository.findByOneTimeCodeId(any())).willReturn(null)

        // When
        val actual = persistenceService.findByDomainId(domainId)

        // Then
        assertThat(actual).isNull()
        verify(oneTimeCodeRepository).findByOneTimeCodeId(domainId)
        verifyNoInteractions(oneTimeCodeEntityMapper)
    }

    @Test
    fun `should save given existing entity`() {
        // Given
        val domainId = UUID.randomUUID()

        val existingEntity = aOneTimeCodeEntity(
            oneTimeCodeId = domainId,
            attempts = 1,
            status = Status.NEW
        )
        given(oneTimeCodeRepository.findByOneTimeCodeId(any())).willReturn(existingEntity)

        val oneTimeCode = aOneTimeCode(
            id = domainId,
            validationAttempts = 2,
            status = DomainStatus.TOO_MANY_VALIDATION_ATTEMPTS
        )

        given(statusEntityMapper.fromDomainToEntity(any())).willReturn(Status.TOO_MANY_VALIDATION_ATTEMPTS)

        // When
        persistenceService.save(oneTimeCode)

        // Then
        verify(oneTimeCodeRepository).findByOneTimeCodeId(domainId)
        verify(statusEntityMapper).fromDomainToEntity(DomainStatus.TOO_MANY_VALIDATION_ATTEMPTS)
        verifyNoInteractions(oneTimeCodeEntityMapper)
    }

    @Test
    fun `should save given entity does not exist`() {
        // Given
        val domainId = UUID.randomUUID()

        given(oneTimeCodeRepository.findByOneTimeCodeId(any())).willReturn(null)

        val oneTimeCode = aOneTimeCode(
            id = domainId,
            validationAttempts = 0,
            status = DomainStatus.NOT_VALIDATED
        )

        val expectedEntity = aOneTimeCodeEntity(
            oneTimeCodeId = domainId,
            attempts = 0,
            status = Status.NEW
        )
        given(oneTimeCodeEntityMapper.fromDomainToEntity(any())).willReturn(expectedEntity)

        val argumentCaptor = ArgumentCaptor.forClass(OneTimeCodeEntity::class.java)

        // When
        persistenceService.save(oneTimeCode)

        // Then
        verify(oneTimeCodeRepository).findByOneTimeCodeId(domainId)
        verify(oneTimeCodeEntityMapper).fromDomainToEntity(oneTimeCode)
        verifyNoInteractions(statusEntityMapper)
        verify(oneTimeCodeRepository).save(argumentCaptor.capture())
    }
}
