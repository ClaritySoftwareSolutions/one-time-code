package uk.co.claritysoftware.onetimecode.app.database.jpa

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import uk.co.claritysoftware.onetimecode.app.database.jpa.entity.OneTimeCodeEntity
import uk.co.claritysoftware.onetimecode.app.database.jpa.mapper.OneTimeCodeEntityMapper
import uk.co.claritysoftware.onetimecode.app.database.jpa.mapper.StatusEntityMapper
import uk.co.claritysoftware.onetimecode.app.database.jpa.repository.OneTimeCodeRepository
import uk.co.claritysoftware.onetimecode.domain.OneTimeCode
import uk.co.claritysoftware.onetimecode.domain.service.OneTimeCodePersistenceService
import java.util.UUID

@Service
class JpaBackedOneTimeCodePersistenceService(
    private val oneTimeCodeRepository: OneTimeCodeRepository,
    private val oneTimeCodeEntityMapper: OneTimeCodeEntityMapper,
    private val statusEntityMapper: StatusEntityMapper
) : OneTimeCodePersistenceService {

    override fun findByDomainId(domainId: UUID): OneTimeCode? {
        return oneTimeCodeRepository.findByOneTimeCodeId(domainId)?.let {
            oneTimeCodeEntityMapper.fromEntityToDomain(it)
        }
    }

    @Transactional
    override fun save(oneTimeCode: OneTimeCode): OneTimeCode {
        oneTimeCodeRepository.findByOneTimeCodeId(oneTimeCode.id)
            ?.updateFromDomain(oneTimeCode) // Update existing entity if it is not null
            ?: run { // Entity not found; create a new one
                val entity = oneTimeCodeEntityMapper.fromDomainToEntity(oneTimeCode)
                oneTimeCodeRepository.save(entity)
            }
        return oneTimeCode
    }

    private fun OneTimeCodeEntity.updateFromDomain(oneTimeCode: OneTimeCode) {
        status = statusEntityMapper.fromDomainToEntity(oneTimeCode.status)
        attempts = oneTimeCode.validationAttempts
    }
}
