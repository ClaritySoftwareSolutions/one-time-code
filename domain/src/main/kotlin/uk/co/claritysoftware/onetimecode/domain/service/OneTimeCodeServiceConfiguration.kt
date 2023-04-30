package uk.co.claritysoftware.onetimecode.domain.service

interface OneTimeCodeServiceConfiguration {

    val characterSet: Set<Char>

    val codeLength: Int

    val codeTtlSeconds: Long

    val maximumValidationAttempts: Int
}
