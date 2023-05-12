package uk.co.claritysoftware.onetimecode.domain.repository

import uk.co.claritysoftware.onetimecode.domain.OneTimeCode
import java.util.UUID

interface OneTimeCodeRepository {

    fun findByDomainId(domainId: UUID): OneTimeCode?

    fun save(oneTimeCode: OneTimeCode): OneTimeCode
}
