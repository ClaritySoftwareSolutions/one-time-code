package uk.co.claritysoftware.onetimecode.mapper

import org.mapstruct.Mapper
import uk.co.claritysoftware.onetimecode.domain.OneTimeCode
import uk.co.claritysoftware.onetimecode.models.OneTimeCodeResponse

@Mapper(uses = [InstantMapper::class])
interface OneTimeCodeMapper {

    fun fromDomainToApi(oneTimeCode: OneTimeCode): OneTimeCodeResponse
}
