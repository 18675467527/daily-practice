package com.mk.webfluxtest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebfluxTestApplicationTests {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void testMonoEndpoint() {
        webTestClient.get()
                .uri("/router/mono")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Hello, Mono from Router!");
    }

    @Test
    void testFluxEndpoint() {
        webTestClient.get()
                .uri("/router/flux")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Integer.class).isEqualTo(List.of(1, 2, 3, 4, 5));
    }

    @Test
    void testOrder() {
        webTestClient.post()
                .uri("/orders")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Hello, Mono from Router!");
    }


}
