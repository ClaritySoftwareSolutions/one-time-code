package uk.co.claritysoftware.onetimecode.domain

import java.util.UUID

class OneTimeCodeNotFoundException(val domainId: UUID) : RuntimeException("One Time Code not found")

class OneTimeCodeExpiredException(val oneTimeCode: OneTimeCode) : RuntimeException("One Time Code expired")

class OneTimeCodeTooManyAttemptsException(val oneTimeCode: OneTimeCode) : RuntimeException("One Time Code has had too many validation attempts")

class OneTimeCodeValidationNotMatchedException(val oneTimeCode: OneTimeCode) : RuntimeException("One Time Code validation code not matched")
