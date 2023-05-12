package uk.co.claritysoftware.onetimecode.domain

import java.time.Instant

fun aOneTimeCode(
    code: String = "ABCDEF",
    expiry: Instant = Instant.EPOCH,
    validationAttempts: Int = 0,
    status: Status = Status.NOT_VALIDATED
): OneTimeCode {
    val oneTimeCode = OneTimeCode(
        code = code,
        expiry = expiry
    ).apply {
        this.status = status
        this.validationAttempts = validationAttempts
    }

    return oneTimeCode
}
