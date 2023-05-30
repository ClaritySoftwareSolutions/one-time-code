package uk.co.claritysoftware.onetimecode.app.rest

import jakarta.validation.Valid
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.HttpStatus.NO_CONTENT
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import uk.co.claritysoftware.onetimecode.app.rest.models.OneTimeCodeResponse
import uk.co.claritysoftware.onetimecode.app.rest.models.ValidateOneTimeCodeRequest
import uk.co.claritysoftware.onetimecode.app.rest.service.OneTimeCodeService
import java.util.UUID

@RestController
@RequestMapping("/one-time-code")
class OneTimeCodeController(
    private val oneTimeCodeService: OneTimeCodeService,
) {

    @PostMapping
    @ResponseStatus(CREATED)
    fun generateOneTimeCode(): OneTimeCodeResponse {
        return oneTimeCodeService.generateOneTimeCode()
    }

    @PostMapping("/{id}")
    @ResponseStatus(NO_CONTENT)
    fun validateOneTimeCode(@PathVariable id: UUID, @RequestBody @Valid request: ValidateOneTimeCodeRequest) {
        oneTimeCodeService.validateOneTimeCode(id, request.code)
    }
}
