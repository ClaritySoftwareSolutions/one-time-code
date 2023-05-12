package uk.co.claritysoftware.onetimecode.rest

import org.springframework.http.HttpStatus.CREATED
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import uk.co.claritysoftware.onetimecode.domain.service.OneTimeCodeService
import uk.co.claritysoftware.onetimecode.mapper.OneTimeCodeMapper
import uk.co.claritysoftware.onetimecode.models.OneTimeCodeResponse

@RestController
@RequestMapping("/one-time-code")
class OneTimeCodeController(
    private val oneTimeCodeService: OneTimeCodeService,
    private val oneTimeCodeMapper: OneTimeCodeMapper,
) {

    @PostMapping
    @ResponseStatus(CREATED)
    fun generateOneTimeCode(): OneTimeCodeResponse {
        return oneTimeCodeService.createOneTimeCode().let {
            oneTimeCodeMapper.fromDomainToApi(it)
        }
    }
}
