package uk.co.claritysoftware.onetimecode.domain

import java.util.UUID

class OneTimeCodeNotFoundException(val domainId: UUID) : RuntimeException()

class OneTimeCodeExpiredException(val oneTimeCode: OneTimeCode) : RuntimeException()

class OneTimeCodeTooManyAttemptsException(val oneTimeCode: OneTimeCode) : RuntimeException()

class OneTimeCodeValidationNotMatchedException(val oneTimeCode: OneTimeCode) : RuntimeException()
