package uk.co.claritysoftware.onetimecode.app.rest

import jakarta.validation.Valid
import mu.KotlinLogging
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import uk.co.claritysoftware.onetimecode.app.rest.exception.OneTimeCodeNotFoundException
import uk.co.claritysoftware.onetimecode.app.rest.exception.OneTimeCodeTooManyAttemptsException
import uk.co.claritysoftware.onetimecode.app.rest.mapper.OneTimeCodeMapper
import uk.co.claritysoftware.onetimecode.app.rest.models.OneTimeCodeResponse
import uk.co.claritysoftware.onetimecode.app.rest.models.ValidateOneTimeCodeRequest
import uk.co.claritysoftware.onetimecode.domain.service.OneTimeCodeService
import java.util.UUID
import uk.co.claritysoftware.onetimecode.domain.OneTimeCodeNotFoundException as DomainOneTimeCodeNotFoundException
import uk.co.claritysoftware.onetimecode.domain.OneTimeCodeTooManyAttemptsException as DomainOneTimeCodeTooManyAttemptsException

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("/one-time-code")
class OneTimeCodeController(
    private val domainService: OneTimeCodeService,
    private val oneTimeCodeMapper: OneTimeCodeMapper
) {

    @PostMapping
    @ResponseStatus(CREATED)
    fun generateOneTimeCode(): OneTimeCodeResponse {
        return domainService.createOneTimeCode().let {
            oneTimeCodeMapper.fromDomainToApi(it)
        }
    }

    @PostMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    fun validateOneTimeCode(@PathVariable id: UUID, @RequestBody @Valid request: ValidateOneTimeCodeRequest) {
        try {
            domainService.validateOneTimeCode(id, request.code)
        } catch (e: DomainOneTimeCodeTooManyAttemptsException) {
            logger.debug { "One Time Code $id has had too many validation attempts" }
            throw OneTimeCodeTooManyAttemptsException(id)
        } catch (e: DomainOneTimeCodeNotFoundException) {
            logger.debug { "One Time Code $id was not found" }
            throw OneTimeCodeNotFoundException(id)
        }
    }
}
