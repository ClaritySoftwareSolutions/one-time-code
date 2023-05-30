package uk.co.claritysoftware.onetimecode.app.database.jpa.mapper

import org.mapstruct.AfterMapping
import org.mapstruct.Mapper
import org.mapstruct.Mapping
import org.mapstruct.MappingTarget
import uk.co.claritysoftware.onetimecode.app.database.jpa.entity.OneTimeCodeEntity
import uk.co.claritysoftware.onetimecode.domain.OneTimeCode
import java.util.UUID

/**
 * Mapper class to map to and from JPA and Domain implementations/representations of a One Time Code
 */
@Mapper(
    uses = [
        StatusEntityMapper::class
    ]
)
abstract class OneTimeCodeEntityMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "oneTimeCodeId", source = "id")
    @Mapping(target = "value", source = "code")
    @Mapping(target = "expires", source = "expiry")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "attempts", source = "validationAttempts")
    @Mapping(target = "dateCreated", ignore = true)
    @Mapping(target = "dateUpdated", ignore = true)
    @Mapping(target = "version", ignore = true)
    abstract fun fromDomainToEntity(oneTimeCode: OneTimeCode): OneTimeCodeEntity

    @Mapping(target = "code", source = "value")
    @Mapping(target = "expiry", source = "expires")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "validationAttempts", source = "attempts")
    abstract fun fromEntityToDomain(oneTimeCodeEntity: OneTimeCodeEntity): OneTimeCode

    @AfterMapping
    protected fun afterMappingFromEntityToDomain(
        oneTimeCodeEntity: OneTimeCodeEntity,
        @MappingTarget oneTimeCode: OneTimeCode
    ) {
        oneTimeCode.setIdViaReflection(oneTimeCodeEntity.oneTimeCodeId)
    }

    /**
     * Extension function on [OneTimeCode] to allow setting the `id` field via reflection.
     * OneTimeCode is the domain object, so it is right that it protects its internal fields, but in this
     * case this mapper needs access to the field to set the value when mapping from the database entity.
     */
    private fun OneTimeCode.setIdViaReflection(id: UUID?) =
        javaClass.getDeclaredField("id").let {
            it.isAccessible = true
            it.set(this, id)
        }
}
