package uk.co.claritysoftware.onetimecode.domain.service

import uk.co.claritysoftware.onetimecode.domain.OneTimeCode
import java.util.UUID

/**
 * Persistence Service for [OneTimeCode] instances
 *
 * Implementations should use the underlying persistence of the application in question, eg: JPA, Mongo, Dynamo, Redis etc
 *
 * Implementations should not throw exceptions. These are not part of the interface and are not checked or handled by
 * [OneTimeCodeService].
 */
interface OneTimeCodePersistenceService {

    /**
     * Find and return the [OneTimeCode] identified by its ID. Returns null if the One Time Code cannot be found.
     */
    fun findByDomainId(domainId: UUID): OneTimeCode?

    /**
     * Save the specified [OneTimeCode] to persistence storage.
     *
     * There is no differentiation between saving a new or updating an existing [OneTimeCode]. Implementations should
     * implement this differentiation if it is relevant to the storage medium in question.
     */
    fun save(oneTimeCode: OneTimeCode): OneTimeCode

    /**
     * Remove the specified [OneTimeCode] from persistence storage.
     */
    fun remove(oneTimeCode: OneTimeCode)
}
