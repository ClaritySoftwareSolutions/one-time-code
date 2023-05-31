package uk.co.claritysoftware.onetimecode.app.rest.service

import mu.KotlinLogging
import org.springframework.stereotype.Service
import uk.co.claritysoftware.onetimecode.app.rest.exception.OneTimeCodeNotFoundException
import uk.co.claritysoftware.onetimecode.app.rest.exception.OneTimeCodeTooManyAttemptsException
import uk.co.claritysoftware.onetimecode.app.rest.mapper.OneTimeCodeMapper
import uk.co.claritysoftware.onetimecode.app.rest.models.OneTimeCodeResponse
import java.util.UUID
import uk.co.claritysoftware.onetimecode.domain.OneTimeCodeNotFoundException as DomainOneTimeCodeNotFoundException
import uk.co.claritysoftware.onetimecode.domain.OneTimeCodeTooManyAttemptsException as DomainOneTimeCodeTooManyAttemptsException
import uk.co.claritysoftware.onetimecode.domain.service.OneTimeCodeService as DomainService

private val logger = KotlinLogging.logger {}

@Service
class OneTimeCodeService(
    private val domainService: DomainService,
    private val oneTimeCodeMapper: OneTimeCodeMapper
) {

    fun generateOneTimeCode(): OneTimeCodeResponse {
        return domainService.createOneTimeCode().let {
            oneTimeCodeMapper.fromDomainToApi(it)
        }
    }

    @Throws(OneTimeCodeNotFoundException::class)
    fun validateOneTimeCode(id: UUID, code: String) {
        try {
            domainService.validateOneTimeCode(id, code)
        } catch (e: DomainOneTimeCodeTooManyAttemptsException) {
            logger.debug { "One Time Code $id has had too many validation attempts" }
            throw OneTimeCodeTooManyAttemptsException(id)
        } catch (e: DomainOneTimeCodeNotFoundException) {
            logger.debug { "One Time Code $id was not found" }
            throw OneTimeCodeNotFoundException(id)
        }
    }
}
