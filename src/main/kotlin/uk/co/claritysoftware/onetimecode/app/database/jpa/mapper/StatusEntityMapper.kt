package uk.co.claritysoftware.onetimecode.app.database.jpa.mapper

import org.mapstruct.InheritInverseConfiguration
import org.mapstruct.Mapper
import org.mapstruct.ValueMapping
import uk.co.claritysoftware.onetimecode.app.database.jpa.entity.Status
import uk.co.claritysoftware.onetimecode.domain.Status as DomainStatus

@Mapper
interface StatusEntityMapper {

    @ValueMapping(source = "NOT_VALIDATED", target = "NEW")
    @ValueMapping(source = "VALIDATED", target = "USED")
    @ValueMapping(source = "EXPIRED", target = "EXPIRED")
    @ValueMapping(source = "TOO_MANY_VALIDATION_ATTEMPTS", target = "TOO_MANY_VALIDATION_ATTEMPTS")
    fun fromDomainToEntity(status: DomainStatus): Status

    @InheritInverseConfiguration
    fun fromEntityToDomain(entityStatus: Status): DomainStatus
}
