package uk.co.claritysoftware.onetimecode.app.database.jpa.mapper

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import uk.co.claritysoftware.onetimecode.app.database.jpa.entity.Status
import uk.co.claritysoftware.onetimecode.domain.Status as DomainStatus

class StatusEntityMapperTest {

    private val mapper = StatusEntityMapperImpl()

    @ParameterizedTest
    @CsvSource(
        value = [
            "NOT_VALIDATED, NEW",
            "VALIDATED, USED",
            "EXPIRED, EXPIRED",
            "TOO_MANY_VALIDATION_ATTEMPTS, TOO_MANY_VALIDATION_ATTEMPTS",
        ]
    )
    fun `should map from domain status to entity status`(domainStatus: DomainStatus, expected: Status) {
        // Given

        // When
        val actual = mapper.fromDomainToEntity(domainStatus)

        // Then
        assertThat(actual).isEqualTo(expected)
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "NEW, NOT_VALIDATED",
            "USED, VALIDATED",
            "EXPIRED, EXPIRED",
            "TOO_MANY_VALIDATION_ATTEMPTS, TOO_MANY_VALIDATION_ATTEMPTS",
        ]
    )
    fun `should map from entity status to domain status`(entityStatus: Status, expected: DomainStatus) {
        // Given

        // When
        val actual = mapper.fromEntityToDomain(entityStatus)

        // Then
        assertThat(actual).isEqualTo(expected)
    }
}
