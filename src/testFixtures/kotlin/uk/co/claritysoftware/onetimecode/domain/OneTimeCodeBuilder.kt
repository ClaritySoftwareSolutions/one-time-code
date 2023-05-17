package uk.co.claritysoftware.onetimecode.domain

import java.time.Instant
import java.util.UUID

fun aOneTimeCode(
    id: UUID = UUID.randomUUID(),
    code: String = "ABCDEF",
    expiry: Instant = Instant.EPOCH,
    validationAttempts: Int = 0,
    status: Status = Status.NOT_VALIDATED
): OneTimeCode {
    val oneTimeCode = OneTimeCode(
        code = code,
        expiry = expiry
    ).apply {
        this.javaClass.getDeclaredField("id").let {
            it.isAccessible = true
            it.set(this, id)
        }
        this.status = status
        this.validationAttempts = validationAttempts
    }

    return oneTimeCode
}
