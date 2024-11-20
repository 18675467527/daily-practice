package com.mk.webfluxtest.handler;

import com.mk.webfluxtest.entry.Order;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author hjm
 * @Date 2024/7/8 14:56
 */
@Service
public class OrderHandler {

    //将数据都放在这个Map里面不操作数据库
    private Map<String, Order> orderMap = new HashMap<>();

    /**
     * 创建订单
     * @param serverRequest
     * @return
     */
    public Mono<ServerResponse> create (ServerRequest serverRequest){
        //body转化成订单
        return  serverRequest.bodyToMono(Order.class)
                .doOnNext(order ->{
                    orderMap.put(order.getId(),order);
                })
                .flatMap(order -> ServerResponse.created(URI.create("/order/" + order.getId())).build());

    }

    /**
     * 获取订单信息
     * @param request
     * @return
     */
    public Mono<ServerResponse> get(ServerRequest request){
        String id = request.pathVariable("id");
        Order order1 = orderMap.get(id);
        return Mono.just(order1)
                .flatMap(order -> ServerResponse.ok().syncBody(order)).switchIfEmpty(ServerResponse.notFound().build());
    }

    /**
     * 获取订单列表
     * @param request
     * @return
     */
    public Mono<ServerResponse> list(ServerRequest request){
        return Mono.just(orderMap.values().stream().collect(Collectors.toList()))
                .flatMap(order -> ServerResponse.ok().syncBody(order))
                .switchIfEmpty(ServerResponse.notFound().build());

    }

}