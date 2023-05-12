package uk.co.claritysoftware.onetimecode.app.database.jpa.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.hibernate.annotations.UuidGenerator
import java.time.Instant
import java.util.UUID

@Table
@Entity
class OneTimeCodeEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    val id: UUID? = null

    @Column
    val oneTimeCodeId: UUID? = null

    @Column
    val value: String? = null

    @Column
    val expires: Instant? = null

    @Column
    val attempts: Int? = null

    @Column
    @Enumerated(value = EnumType.STRING)
    val status: Status? = null
}

enum class Status {
    NEW,
    USED,
    EXPIRED,
    TOO_MANY_VALIDATION_ATTEMPTS
}
