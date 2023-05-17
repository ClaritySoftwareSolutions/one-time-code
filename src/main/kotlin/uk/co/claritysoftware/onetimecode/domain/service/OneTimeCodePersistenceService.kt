package uk.co.claritysoftware.onetimecode.domain.service

import uk.co.claritysoftware.onetimecode.domain.OneTimeCode
import java.util.UUID

interface OneTimeCodePersistenceService {

    fun findByDomainId(domainId: UUID): OneTimeCode?

    fun save(oneTimeCode: OneTimeCode): OneTimeCode
}
