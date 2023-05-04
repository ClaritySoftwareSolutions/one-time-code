package uk.co.claritysoftware.onetimecode.rest

import org.springframework.http.HttpStatus.CREATED
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import uk.co.claritysoftware.onetimecode.domain.service.OneTimeCodeService
import uk.co.claritysoftware.onetimecode.models.OneTimeCodeResponse
import java.time.ZoneOffset

@RestController
@RequestMapping("/one-time-code")
class OneTimeCodeController(
    private val oneTimeCodeService: OneTimeCodeService
) {

    @PostMapping
    @ResponseStatus(CREATED)
    fun generateOneTimeCode(): OneTimeCodeResponse {
        return oneTimeCodeService.createOneTimeCode().let {
            OneTimeCodeResponse(
                id = it.id,
                code = it.code,
                expiry = it.expiry.atOffset(ZoneOffset.UTC)
            )
        }
    }
}
