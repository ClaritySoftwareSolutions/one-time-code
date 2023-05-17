package uk.co.claritysoftware.onetimecode.domain.service

import uk.co.claritysoftware.onetimecode.domain.OneTimeCode
import java.time.Clock
import java.time.Instant

open class OneTimeCodeFactory(private val clock: Clock) {

    fun createOneTimeCode(characterSet: Set<Char>, codeLength: Int, codeTtlSeconds: Long): OneTimeCode {
        val expiry = Instant.now(clock).plusSeconds(codeTtlSeconds)
        val code = IntRange(1, codeLength)
            .map { characterSet.random() }
            .joinToString("")
        return OneTimeCode(code, expiry)
    }
}
