package uk.co.claritysoftware.onetimecode.app.database.jpa.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import uk.co.claritysoftware.onetimecode.app.database.jpa.entity.OneTimeCodeEntity
import java.util.UUID

@Repository
interface OneTimeCodeRepository : JpaRepository<OneTimeCodeEntity, UUID> {
    fun findByOneTimeCodeId(oneTimeCodeId: UUID): OneTimeCodeEntity?
}
