package uk.co.claritysoftware.onetimecode.domain.service

/**
 * Interface defining the configuration for the [OneTimeCodeService]
 *
 * Typical implementations would be a simple kotlin data class providing the properties defined in this interface.
 */
interface OneTimeCodeServiceConfiguration {

    val characterSet: Set<Char>

    val codeLength: Int

    val codeTtlSeconds: Long

    val maximumValidationAttempts: Int
}
