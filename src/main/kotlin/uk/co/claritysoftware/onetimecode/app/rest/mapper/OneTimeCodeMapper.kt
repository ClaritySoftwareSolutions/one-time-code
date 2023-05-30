package uk.co.claritysoftware.onetimecode.app.rest.mapper

import org.mapstruct.Mapper
import uk.co.claritysoftware.onetimecode.app.rest.models.OneTimeCodeResponse
import uk.co.claritysoftware.onetimecode.domain.OneTimeCode

/**
 * Mapper class to map to and from REST API and Domain implementations/representations of a One Time Code
 */
@Mapper(uses = [InstantMapper::class])
interface OneTimeCodeMapper {

    fun fromDomainToApi(oneTimeCode: OneTimeCode): OneTimeCodeResponse
}
