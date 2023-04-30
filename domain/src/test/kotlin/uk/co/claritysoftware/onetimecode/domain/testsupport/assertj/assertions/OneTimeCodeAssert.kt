package uk.co.claritysoftware.onetimecode.domain.testsupport.assertj.assertions

import org.assertj.core.api.AbstractAssert
import uk.co.claritysoftware.onetimecode.domain.OneTimeCode
import uk.co.claritysoftware.onetimecode.domain.Status
import java.time.Instant

fun assertThat(actual: OneTimeCode?) = OneTimeCodeAssert(actual)

class OneTimeCodeAssert(actual: OneTimeCode?) :
    AbstractAssert<OneTimeCodeAssert, OneTimeCode?>(actual, OneTimeCodeAssert::class.java) {

    fun hasCodeLength(expected: Int): OneTimeCodeAssert {
        isNotNull
        with(actual!!) {
            if (code.length != expected) {
                failWithMessage("Expected code length to be $expected but was ${code.length}")
            }
        }
        return this
    }

    fun codeContainsOnlyCharactersFrom(expected: Set<Char>): OneTimeCodeAssert {
        isNotNull
        with(actual!!) {
            val charactersInCode = code.toCharArray().toSet()
            if (!charactersInCode.all { expected.contains(it) }) {
                failWithMessage("Expected code to only contain characters from $expected but was $code")
            }
        }
        return this
    }

    fun hasCode(expected: String): OneTimeCodeAssert {
        isNotNull
        with(actual!!) {
            if (code != expected) {
                failWithMessage("Expected code to be $expected but was $code")
            }
        }
        return this
    }

    fun hasValidationAttempts(expected: Int): OneTimeCodeAssert {
        isNotNull
        with(actual!!) {
            if (validationAttempts != expected) {
                failWithMessage("Expected validationAttempts to be $expected but was $validationAttempts")
            }
        }
        return this
    }

    fun hasExpiry(expected: Instant): OneTimeCodeAssert {
        isNotNull
        with(actual!!) {
            if (expiry != expected) {
                failWithMessage("Expected expiry to be $expected but was $expiry")
            }
        }
        return this
    }

    fun isValidated(): OneTimeCodeAssert {
        isNotNull
        with(actual!!) {
            if (status != Status.VALIDATED) {
                failWithMessage("Expected status to be VALIDATED but was $status")
            }
        }
        return this
    }

    fun hasStatus(expected: Status): OneTimeCodeAssert {
        isNotNull
        with(actual!!) {
            if (status != expected) {
                failWithMessage("Expected status to be $expected but was $status")
            }
        }
        return this
    }

    fun isNotValidated(): OneTimeCodeAssert {
        isNotNull
        with(actual!!) {
            if (status != Status.NOT_VALIDATED) {
                failWithMessage("Expected status to be NOT_VALIDATED but was $status")
            }
        }
        return this
    }
}
