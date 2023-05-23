package uk.co.claritysoftware.onetimecode.app.database.jpa.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Version
import org.hibernate.Hibernate
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.hibernate.annotations.UuidGenerator
import java.time.Instant
import java.util.UUID

@Table(name = "one_time_code")
@Entity
class OneTimeCodeEntity(

    @Id
    @GeneratedValue
    @UuidGenerator
    var id: UUID? = null,

    @Column
    var oneTimeCodeId: UUID? = null,

    @Column(name = "\"value\"")
    var value: String? = null,

    @Column
    var expires: Instant? = null,

    @Column
    var attempts: Int? = null,

    @Column
    @Enumerated(value = EnumType.STRING)
    var status: Status? = null,

    @Column
    @CreationTimestamp
    var dateCreated: Instant? = null,

    @Column
    @UpdateTimestamp
    var dateUpdated: Instant? = null,

    @Column
    @Version
    var version: Int? = null
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || Hibernate.getClass(this) != Hibernate.getClass(other)) return false
        other as OneTimeCodeEntity

        return id != null && id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    override fun toString(): String {
        return this::class.simpleName + "(id = $id, oneTimeCodeId = $oneTimeCodeId, value = $value, expires = $expires, attempts = $attempts, status = $status)"
    }
}

enum class Status {
    NEW,
    USED,
    EXPIRED,
    TOO_MANY_VALIDATION_ATTEMPTS
}
