package uk.co.claritysoftware.onetimecode.domain.service

import uk.co.claritysoftware.onetimecode.domain.OneTimeCode
import uk.co.claritysoftware.onetimecode.domain.OneTimeCodeNotFoundException
import uk.co.claritysoftware.onetimecode.domain.OneTimeCodeTooManyAttemptsException
import java.time.Clock
import java.time.Instant
import java.util.UUID

class OneTimeCodeServiceImpl(
    private val factory: OneTimeCodeFactory,
    private val configuration: OneTimeCodeServiceConfiguration,
    private val persistenceService: OneTimeCodePersistenceService,
    private val clock: Clock
) : OneTimeCodeService {

    override fun createOneTimeCode(): OneTimeCode =
        with(configuration) {
            factory.createOneTimeCode(characterSet, codeLength, codeTtlSeconds)
        }.also {
            persistenceService.save(it)
        }

    override fun validateOneTimeCode(domainId: UUID, code: String) {
        val oneTimeCode = persistenceService.findByDomainId(domainId)
            ?: throw OneTimeCodeNotFoundException(domainId)

        try {
            oneTimeCode.validate(code, Instant.now(clock), configuration.maximumValidationAttempts)
            persistenceService.save(oneTimeCode)
        } catch (e: RuntimeException) {
            if (e is OneTimeCodeTooManyAttemptsException) {
                persistenceService.remove(oneTimeCode)
            } else {
                persistenceService.save(oneTimeCode)
            }
            throw e
        }
    }
}
