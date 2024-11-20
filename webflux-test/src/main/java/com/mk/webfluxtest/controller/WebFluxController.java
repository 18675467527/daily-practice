package com.mk.webfluxtest.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @Author hjm
 * @Date 2024/7/8 14:29
 */

@RestController
public class WebFluxController {

    @GetMapping("/mono")
    public Mono<String> monoExample() {
        return Mono.just("Hello, Mono!");
    }

    @GetMapping("/flux")
    public Flux<Integer> fluxExample() {
        return Flux.just(1, 2, 3, 4, 5);
    }

}