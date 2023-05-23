package uk.co.claritysoftware.onetimecode.app.rest

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.reactive.server.WebTestClient
import uk.co.claritysoftware.onetimecode.app.assertj.assertions.assertThat
import uk.co.claritysoftware.onetimecode.app.rest.models.OneTimeCodeResponse

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class GenerateOneTimeCodeIntegrationTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Test
    fun `should generate one time code`() {
        // Given

        // When
        val response = webTestClient
            .post()
            .uri("/one-time-code")
            .exchange()
            .expectStatus().isCreated
            .returnResult(OneTimeCodeResponse::class.java)

        // Then
        val oneTimeCode = response.responseBody.blockFirst()
        assertThat(oneTimeCode).isNotNull()
    }
}
