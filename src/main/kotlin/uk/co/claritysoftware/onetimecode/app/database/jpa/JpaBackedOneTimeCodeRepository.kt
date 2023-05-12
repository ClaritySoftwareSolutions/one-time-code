package uk.co.claritysoftware.onetimecode.app.database.jpa

import uk.co.claritysoftware.onetimecode.domain.OneTimeCode
import uk.co.claritysoftware.onetimecode.domain.repository.OneTimeCodeRepository
import java.util.UUID

class JpaBackedOneTimeCodeRepository : OneTimeCodeRepository {
    override fun findByDomainId(domainId: UUID): OneTimeCode? {
        TODO("Not yet implemented")
    }

    override fun save(oneTimeCode: OneTimeCode): OneTimeCode {
        TODO("Not yet implemented")
    }
}
