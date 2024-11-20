package com.mk.webfluxtest.router;

import com.mk.webfluxtest.handler.OrderHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * @Author hjm
 * @Date 2024/7/8 14:53
 */
@Configuration
public class WebFluxOrderRouter {

    @Bean
    public RouterFunction<ServerResponse> routeExample1(OrderHandler orderHandler) {
        return nest(
                // 判断请求路径是否匹配指定的前缀
                path("/orders"),
                //如果匹配成功，则路由到这个函数
                nest(accept(MediaType.APPLICATION_JSON),// 判断请求报文头字段accept是否匹配APPLICATION_JSON
                        // 如果匹配则路由到下面的路由函数 ，将/orders/{id} 路由到handler的get
                        route(GET("/{id}"),orderHandler::get))
                        // 如果get请求 /orders ，则路由到orderhandler.list
                        .andRoute(method(HttpMethod.GET),orderHandler::list)
                        // 如果contentType匹配，并路径匹配orders,则路由到这个函数

                        .andNest(contentType(MediaType.APPLICATION_JSON),
                                //如果是POST请求/orders，则路由到handler的create方法
                                route(POST("/"),orderHandler::create)));
    }


}