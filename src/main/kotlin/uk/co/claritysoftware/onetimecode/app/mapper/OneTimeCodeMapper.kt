package uk.co.claritysoftware.onetimecode.app.mapper

import org.mapstruct.Mapper
import uk.co.claritysoftware.onetimecode.app.rest.models.OneTimeCodeResponse
import uk.co.claritysoftware.onetimecode.domain.OneTimeCode

@Mapper(uses = [InstantMapper::class])
interface OneTimeCodeMapper {

    fun fromDomainToApi(oneTimeCode: OneTimeCode): OneTimeCodeResponse
}
