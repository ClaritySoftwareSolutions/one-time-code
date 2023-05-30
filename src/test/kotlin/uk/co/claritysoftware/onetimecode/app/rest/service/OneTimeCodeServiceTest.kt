package uk.co.claritysoftware.onetimecode.app.rest.service

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowableOfType
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.mockito.kotlin.verify
import uk.co.claritysoftware.onetimecode.app.assertj.assertions.assertThat
import uk.co.claritysoftware.onetimecode.app.rest.exception.OneTimeCodeNotFoundException
import uk.co.claritysoftware.onetimecode.app.rest.mapper.OneTimeCodeMapper
import uk.co.claritysoftware.onetimecode.app.rest.models.aOneTimeCodeResponse
import uk.co.claritysoftware.onetimecode.domain.aOneTimeCode
import java.util.UUID
import uk.co.claritysoftware.onetimecode.domain.OneTimeCodeNotFoundException as DomainOneTimeCodeNotFoundException
import uk.co.claritysoftware.onetimecode.domain.service.OneTimeCodeService as DomainService

@ExtendWith(MockitoExtension::class)
class OneTimeCodeServiceTest {

    @InjectMocks
    private lateinit var service: OneTimeCodeService

    @Mock
    private lateinit var domainService: DomainService

    @Mock
    private lateinit var oneTimeCodeMapper: OneTimeCodeMapper

    @Nested
    inner class GenerateOneTimeCode {
        @Test
        fun `should generate one time code`() {
            // Given
            val domainOneTimeCode = aOneTimeCode()
            given(domainService.createOneTimeCode()).willReturn(domainOneTimeCode)

            val restApiOneTimeCode = aOneTimeCodeResponse()
            given(oneTimeCodeMapper.fromDomainToApi(any())).willReturn(restApiOneTimeCode)

            // When
            val actual = service.generateOneTimeCode()

            // Then
            assertThat(actual).isEqualTo(restApiOneTimeCode)
            verify(domainService).createOneTimeCode()
            verify(oneTimeCodeMapper).fromDomainToApi(domainOneTimeCode)
        }
    }

    @Nested
    inner class ValidateOneTimeCode {
        @Test
        fun `should validate one time code`() {
            // Given
            val id = UUID.randomUUID()
            val code = "ABCD"

            // When
            service.validateOneTimeCode(id, code)

            // Then
            verify(domainService).validateOneTimeCode(id, code)
        }

        @Test
        fun `should not validate one time code given domain throws not found exception`() {
            // Given
            val id = UUID.randomUUID()
            val code = "ABCD"

            val domainException = DomainOneTimeCodeNotFoundException(id)
            given(domainService.validateOneTimeCode(any(), any())).willThrow(
                domainException
            )

            // When
            val exception = catchThrowableOfType(
                { service.validateOneTimeCode(id, code) },
                OneTimeCodeNotFoundException::class.java
            )

            // Then
            assertThat(exception).hasMessage("One Time Code not found")
            verify(domainService).validateOneTimeCode(id, code)
        }
    }
}
