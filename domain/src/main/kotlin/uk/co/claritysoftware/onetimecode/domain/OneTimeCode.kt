package uk.co.claritysoftware.onetimecode.domain

import uk.co.claritysoftware.onetimecode.domain.Status.EXPIRED
import uk.co.claritysoftware.onetimecode.domain.Status.NOT_VALIDATED
import uk.co.claritysoftware.onetimecode.domain.Status.TOO_MANY_VALIDATION_ATTEMPTS
import uk.co.claritysoftware.onetimecode.domain.Status.VALIDATED
import java.time.Instant
import java.util.UUID
import kotlin.jvm.Throws

class OneTimeCode private constructor(
    val id: UUID,
    val code: String,
    val expiry: Instant,
    var status: Status = NOT_VALIDATED,
    var validationAttempts: Int = 0
) {
    constructor(code: String, expiry: Instant) : this(
        id = UUID.randomUUID(),
        code = code,
        expiry = expiry
    )

    @Throws(
        OneTimeCodeExpiredException::class,
        OneTimeCodeTooManyAttemptsException::class,
        OneTimeCodeValidationNotMatchedException::class
    )
    fun validate(code: String, now: Instant, maximumValidationAttempts: Int) {
        checkCurrentStatus()

        incrementValidationAttempts()
        checkValidationAttempts(maximumValidationAttempts)
        checkExpiry(now)
        validateCode(code)
    }

    @Throws(
        OneTimeCodeExpiredException::class,
        OneTimeCodeTooManyAttemptsException::class
    )
    private fun checkCurrentStatus() {
        when (status) {
            EXPIRED -> throw OneTimeCodeExpiredException(this)
            TOO_MANY_VALIDATION_ATTEMPTS -> throw OneTimeCodeTooManyAttemptsException(this)
            else -> {}
        }
    }

    @Throws(OneTimeCodeExpiredException::class)
    private fun checkExpiry(instant: Instant) {
        if (expiry.isBefore(instant)) {
            status = EXPIRED
            throw OneTimeCodeExpiredException(this)
        }
    }

    @Throws(OneTimeCodeValidationNotMatchedException::class)
    private fun validateCode(code: String) {
        if (this.code == code) {
            status = VALIDATED
        } else {
            status = NOT_VALIDATED
            throw OneTimeCodeValidationNotMatchedException(this)
        }
    }

    @Throws(OneTimeCodeTooManyAttemptsException::class)
    private fun checkValidationAttempts(maximumValidationAttempts: Int) {
        if (validationAttempts > maximumValidationAttempts) {
            status = TOO_MANY_VALIDATION_ATTEMPTS
            throw OneTimeCodeTooManyAttemptsException(this)
        }
    }

    private fun incrementValidationAttempts() {
        validationAttempts++
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this.javaClass != other.javaClass) return false
        other as OneTimeCode

        return id == other.id
    }

    override fun hashCode(): Int = javaClass.hashCode()

    @Override
    override fun toString(): String {
        return this::class.simpleName + "(id = $id, code = $code, expiry = $expiry, status = $status, validationAttempts = $validationAttempts)"
    }
}

enum class Status {
    NOT_VALIDATED,
    VALIDATED,
    EXPIRED,
    TOO_MANY_VALIDATION_ATTEMPTS
}
