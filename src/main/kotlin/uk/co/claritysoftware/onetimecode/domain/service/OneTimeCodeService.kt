package uk.co.claritysoftware.onetimecode.domain.service

import uk.co.claritysoftware.onetimecode.domain.OneTimeCode
import uk.co.claritysoftware.onetimecode.domain.OneTimeCodeExpiredException
import uk.co.claritysoftware.onetimecode.domain.OneTimeCodeNotFoundException
import uk.co.claritysoftware.onetimecode.domain.OneTimeCodeTooManyAttemptsException
import uk.co.claritysoftware.onetimecode.domain.OneTimeCodeValidationNotMatchedException
import java.time.Clock
import java.time.Instant
import java.util.UUID

/**
 * Service class exposing methods that implement the business rules for the One Time Code domain, and is how applications
 * must create and manage [OneTimeCode]'s.
 *
 * Applications using One Time Codes must new up an instance of this class providing implementations of
 * [OneTimeCodeServiceConfiguration] and [OneTimeCodePersistenceService], and a [Clock].
 *
 * This class is deliberately final so that it cannot be subclassed, ensuring that the business rules stay within the
 * domain.
 */
class OneTimeCodeService(
    private val configuration: OneTimeCodeServiceConfiguration,
    private val persistenceService: OneTimeCodePersistenceService,
    private val clock: Clock
) {

    /**
     * Creates and returns a new [OneTimeCode] using the configured properties.
     * The new One Time Code is saved to the persistence service implementation.
     */
    fun createOneTimeCode(): OneTimeCode =
        with(configuration) {
            createOneTimeCode(characterSet, codeLength, codeTtlSeconds)
        }.also {
            persistenceService.save(it)
        }

    /**
     * Validate a [OneTimeCode] given it's ID and a code to attempt validation with.
     *
     * Validating a One Time Code, whether successful or not, increments the One Time Code's count of validation attempts
     * and the One Time Code is updated in the persistence service.
     *
     * If the One Time Code has expired, validation is not checked, amd the One Time Code is removed from the
     * persistence service.
     *
     * If the validation attempt fails, and this is the last validation attempt (based on the validation attempt count vs.
     * the configuration's maximum validation attempts), the One Time Code is removed from the persistence service.
     *
     * @throws OneTimeCodeNotFoundException if the One Time Code cannot be found by its ID by the persistence service
     * @throws OneTimeCodeTooManyAttemptsException if the validation fails and this is the last validation attempt
     * @throws OneTimeCodeValidationNotMatchedException if the validation fails as this is not the last validation attempt
     * @throws OneTimeCodeExpiredException if the One Time Code has expired
     */
    @Throws(
        OneTimeCodeNotFoundException::class,
        OneTimeCodeExpiredException::class,
        OneTimeCodeTooManyAttemptsException::class,
        OneTimeCodeValidationNotMatchedException::class
    )
    fun validateOneTimeCode(domainId: UUID, code: String) {
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

    private fun createOneTimeCode(characterSet: Set<Char>, codeLength: Int, codeTtlSeconds: Long): OneTimeCode {
        val expiry = Instant.now(clock).plusSeconds(codeTtlSeconds)
        val code = IntRange(1, codeLength)
            .map { characterSet.random() }
            .joinToString("")
        return OneTimeCode(code, expiry)
    }
}
