package com.mk.webfluxtest.handler;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

/**
 * @Author hjm
 * @Date 2024/7/8 14:34
 */

@Service
public class WebFluxHandler {

    public Mono<ServerResponse> monoExample(ServerRequest request) {
        return ok().contentType(MediaType.TEXT_PLAIN)
                .bodyValue("Hello, Mono from Router!");
    }

    public Mono<ServerResponse> fluxExample(ServerRequest request) {
        return ok().contentType(MediaType.APPLICATION_JSON)
                .body(Flux.just(1, 2, 3, 4, 5).collectList(), List.class);
    }

}