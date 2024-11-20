package com.mk.webfluxtest.router;

import com.mk.webfluxtest.handler.OrderHandler;
import com.mk.webfluxtest.handler.WebFluxHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * @Author hjm
 * @Date 2024/7/8 14:33
 */
@Configuration
public class WebFluxRouter {

    @Bean
    public RouterFunction<ServerResponse> routeExample(WebFluxHandler handler) {
        return route()
                .GET("/router/mono", handler::monoExample)
                .GET("/router/flux", handler::fluxExample)
                .build();
    }


}