package uk.co.claritysoftware.onetimecode.app.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import uk.co.claritysoftware.onetimecode.domain.service.OneTimeCodeFactory
import uk.co.claritysoftware.onetimecode.domain.service.OneTimeCodePersistenceService
import uk.co.claritysoftware.onetimecode.domain.service.OneTimeCodeService
import uk.co.claritysoftware.onetimecode.domain.service.OneTimeCodeServiceConfiguration
import uk.co.claritysoftware.onetimecode.domain.service.OneTimeCodeServiceImpl
import java.time.Clock

/**
 * Configuration class responsible for providing domain bean implementations
 */
@Configuration
class DomainConfiguration {

    @Bean
    fun oneTimeCodeFactory(clock: Clock): OneTimeCodeFactory =
        OneTimeCodeFactory(clock)

    @Bean
    fun domainOneTimeCodeService(
        oneTimeCodeFactory: OneTimeCodeFactory,
        serviceConfiguration: OneTimeCodeServiceConfiguration,
        oneTimeCodePersistenceService: OneTimeCodePersistenceService,
        clock: Clock
    ): OneTimeCodeService =
        OneTimeCodeServiceImpl(oneTimeCodeFactory, serviceConfiguration, oneTimeCodePersistenceService, clock)
}

@ConfigurationProperties(prefix = "app")
@EnableConfigurationProperties
data class OneTimeCodeServiceConfigurationImpl(
    override val characterSet: Set<Char>,
    override val codeLength: Int,
    override val codeTtlSeconds: Long,
    override val maximumValidationAttempts: Int
) : OneTimeCodeServiceConfiguration
