package uk.co.claritysoftware.onetimecode.app.database.jpa.entity

import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

fun aOneTimeCodeEntity(
    id: UUID? = null,
    oneTimeCodeId: UUID = UUID.randomUUID(),
    value: String = "ABCD",
    expires: Instant = Instant.now().plus(10, ChronoUnit.MINUTES),
    attempts: Int = 0,
    status: Status = Status.NEW,
    dateCreated: Instant? = null,
    dateUpdated: Instant? = null,
    version: Int? = null
) = OneTimeCodeEntity(
    id = id,
    oneTimeCodeId = oneTimeCodeId,
    value = value,
    expires = expires,
    attempts = attempts,
    status = status,
    dateCreated = dateCreated,
    dateUpdated = dateUpdated,
    version = version
)
