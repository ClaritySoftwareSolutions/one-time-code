package uk.co.claritysoftware.onetimecode.common

import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Mono

fun <T : Any> WebTestClient.RequestBodySpec.withBody(requestBody: T): WebTestClient.RequestBodySpec =
    body(
        Mono.just(requestBody),
        requestBody.javaClass
    ) as WebTestClient.RequestBodySpec
