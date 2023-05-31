package uk.co.claritysoftware.onetimecode.app.rest

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
import uk.co.claritysoftware.onetimecode.app.rest.mapper.OneTimeCodeMapper
import uk.co.claritysoftware.onetimecode.app.rest.models.aOneTimeCodeResponse
import uk.co.claritysoftware.onetimecode.app.rest.models.adValidateOneTimeCodeRequest
import uk.co.claritysoftware.onetimecode.domain.OneTimeCodeNotFoundException
import uk.co.claritysoftware.onetimecode.domain.OneTimeCodeTooManyAttemptsException
import uk.co.claritysoftware.onetimecode.domain.aOneTimeCode
import uk.co.claritysoftware.onetimecode.domain.service.OneTimeCodeService
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class OneTimeCodeControllerTest {

    @InjectMocks
    private lateinit var controller: OneTimeCodeController

    @Mock
    private lateinit var domainService: OneTimeCodeService

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
            val actual = controller.generateOneTimeCode()

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
            val request = adValidateOneTimeCodeRequest(
                code = code
            )

            // When
            controller.validateOneTimeCode(id, request)

            // Then
            verify(domainService).validateOneTimeCode(id, code)
        }

        @Test
        fun `should not validate one time code given domain throws not found exception`() {
            // Given
            val id = UUID.randomUUID()
            val code = "ABCD"

            val domainException = OneTimeCodeNotFoundException(id)
            given(domainService.validateOneTimeCode(any(), any())).willThrow(
                domainException
            )

            val request = adValidateOneTimeCodeRequest(
                code = code
            )

            // When
            val exception = catchThrowableOfType(
                { controller.validateOneTimeCode(id, request) },
                OneTimeCodeNotFoundException::class.java
            )

            // Then
            assertThat(exception).isEqualTo(domainException)
            verify(domainService).validateOneTimeCode(id, code)
        }

        @Test
        fun `should not validate one time code given domain throws too many attempts exception`() {
            // Given
            val id = UUID.randomUUID()
            val code = "ABCD"

            val oneTimeCode = aOneTimeCode(id = id)
            val domainException = OneTimeCodeTooManyAttemptsException(oneTimeCode)
            given(domainService.validateOneTimeCode(any(), any())).willThrow(
                domainException
            )

            val request = adValidateOneTimeCodeRequest(
                code = code
            )

            // When
            val exception = catchThrowableOfType(
                { controller.validateOneTimeCode(id, request) },
                OneTimeCodeTooManyAttemptsException::class.java
            )

            // Then
            assertThat(exception).isEqualTo(domainException)
            verify(domainService).validateOneTimeCode(id, code)
        }
    }
}
