package uk.co.claritysoftware.onetimecode.domain.service

import uk.co.claritysoftware.onetimecode.domain.OneTimeCode
import uk.co.claritysoftware.onetimecode.domain.OneTimeCodeExpiredException
import uk.co.claritysoftware.onetimecode.domain.OneTimeCodeNotFoundException
import uk.co.claritysoftware.onetimecode.domain.OneTimeCodeTooManyAttemptsException
import uk.co.claritysoftware.onetimecode.domain.OneTimeCodeValidationNotMatchedException
import java.util.UUID
import kotlin.jvm.Throws

interface OneTimeCodeService {

    fun createOneTimeCode(): OneTimeCode

    @Throws(
        OneTimeCodeNotFoundException::class,
        OneTimeCodeExpiredException::class,
        OneTimeCodeTooManyAttemptsException::class,
        OneTimeCodeValidationNotMatchedException::class
    )
    fun validateOneTimeCode(domainId: UUID, code: String)
}
