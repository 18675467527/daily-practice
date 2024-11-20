package com.mk.webfluxtest.handler;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

/**
 * @Author hjm
 * @Date 2024/7/8 14:42
 */
@Component
@Order(-2)
public class WebFluxExceptionHandler implements WebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        if (ex instanceof ResponseStatusException) {
            return handleResponseStatusException(exchange, (ResponseStatusException) ex);
        }

        // Handle other types of exceptions if needed
        System.out.println("============================");

        return Mono.error(ex);
    }

    private Mono<Void> handleResponseStatusException(ServerWebExchange exchange, ResponseStatusException ex) {
        exchange.getResponse().setStatusCode(ex.getStatusCode());
        return exchange.getResponse().setComplete();
    }
}