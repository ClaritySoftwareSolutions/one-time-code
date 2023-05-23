package uk.co.claritysoftware.onetimecode.app.assertj.assertions

import org.assertj.core.api.AbstractAssert
import uk.co.claritysoftware.onetimecode.app.rest.models.OneTimeCodeResponse
import java.time.OffsetDateTime

fun assertThat(actual: OneTimeCodeResponse?) = OneTimeCodeResponseAssert(actual)

class OneTimeCodeResponseAssert(actual: OneTimeCodeResponse?) :
    AbstractAssert<OneTimeCodeResponseAssert, OneTimeCodeResponse?>(actual, OneTimeCodeResponseAssert::class.java) {

    fun hasCodeLength(expected: Int): OneTimeCodeResponseAssert {
        isNotNull
        with(actual!!) {
            if (code.length != expected) {
                failWithMessage("Expected code length to be $expected but was ${code.length}")
            }
        }
        return this
    }

    fun codeContainsOnlyCharactersFrom(expected: Set<Char>): OneTimeCodeResponseAssert {
        isNotNull
        with(actual!!) {
            val charactersInCode = code.toCharArray().toSet()
            if (!charactersInCode.all { expected.contains(it) }) {
                failWithMessage("Expected code to only contain characters from $expected but was $code")
            }
        }
        return this
    }

    fun hasCode(expected: String): OneTimeCodeResponseAssert {
        isNotNull
        with(actual!!) {
            if (code != expected) {
                failWithMessage("Expected code to be $expected but was $code")
            }
        }
        return this
    }

    fun hasExpiry(expected: OffsetDateTime): OneTimeCodeResponseAssert {
        isNotNull
        with(actual!!) {
            if (expiry != expected) {
                failWithMessage("Expected expiry to be $expected but was $expiry")
            }
        }
        return this
    }
}
