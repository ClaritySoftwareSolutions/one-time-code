package uk.co.claritysoftware.onetimecode.domain.repository

import uk.co.claritysoftware.onetimecode.domain.OneTimeCode
import java.util.UUID

class OneTimeCodeRepositoryImpl : OneTimeCodeRepository {
    override fun findByDomainId(domainId: UUID): OneTimeCode? {
        TODO("Not yet implemented")
    }

    override fun save(oneTimeCode: OneTimeCode): OneTimeCode {
        TODO("Not yet implemented")
    }
}
