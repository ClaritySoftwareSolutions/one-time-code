package uk.co.claritysoftware.onetimecode.app.rest

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus.GONE
import org.springframework.test.web.reactive.server.WebTestClient
import uk.co.claritysoftware.onetimecode.app.assertj.assertions.assertThat
import uk.co.claritysoftware.onetimecode.app.database.jpa.entity.aOneTimeCodeEntity
import uk.co.claritysoftware.onetimecode.app.database.jpa.repository.OneTimeCodeRepository
import uk.co.claritysoftware.onetimecode.app.rest.models.ErrorResponse
import uk.co.claritysoftware.onetimecode.app.rest.models.adValidateOneTimeCodeRequest
import uk.co.claritysoftware.onetimecode.common.withBody
import java.util.UUID

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class ValidateOneTimeCodeIntegrationTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var repository: OneTimeCodeRepository

    @Test
    fun `should not validate one time code given one time code does not exist`() {
        // Given
        val oneTimeCodeId = UUID.randomUUID()

        // When
        val response = webTestClient
            .post()
            .uri("/one-time-code/{id}", oneTimeCodeId)
            .withBody(adValidateOneTimeCodeRequest())
            .exchange()
            .expectStatus().isNotFound
            .returnResult(ErrorResponse::class.java)

        // Then
        val actual = response.responseBody.blockFirst()
        assertThat(actual)
            .hasStatus(404)
            .hasError("Not Found")
            .hasMessage("One Time Code not found")
    }

    @Test
    fun `should not validate one time code given last attempt at validation with incorrect code`() {
        // Given
        val oneTimeCodeId = UUID.randomUUID()
        val oneTimeCodeEntity = aOneTimeCodeEntity(
            oneTimeCodeId = oneTimeCodeId,
            attempts = 2,
            value = "ABCD"
        )
        repository.save(oneTimeCodeEntity)

        val validationRequest = adValidateOneTimeCodeRequest(
            code = "XYZ"
        )

        // When
        val response = webTestClient
            .post()
            .uri("/one-time-code/{id}", oneTimeCodeId)
            .withBody(validationRequest)
            .exchange()
            .expectStatus().isEqualTo(GONE)
            .returnResult(ErrorResponse::class.java)

        // Then
        val actual = response.responseBody.blockFirst()
        assertThat(actual)
            .hasStatus(410)
            .hasError("Gone")
            .hasMessage("One Time Code has failed validation and has been removed")

        // assert that the One Time Code has been deleted as well
        assertThat(repository.findByOneTimeCodeId(oneTimeCodeId)).isNull()
    }
}
