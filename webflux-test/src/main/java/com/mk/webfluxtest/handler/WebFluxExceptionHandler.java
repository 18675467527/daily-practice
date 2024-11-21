package com.mk.webfluxtest.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mk.webfluxtest.common.ResponseInfoVO;
import com.mk.webfluxtest.exception.ParamException;
import com.mk.webfluxtest.exception.ServiceException;
import lombok.SneakyThrows;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
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
        // Handle other types of exceptions if needed
        System.out.println("============================进入异常处理");
        if (ex instanceof ResponseStatusException) {
            return handleResponseStatusException(exchange, (ResponseStatusException) ex);
        }
        // 参数异常
        if(ex instanceof ParamException){
            return handleParamException(exchange, (ParamException)ex);
        }

        // 服务异常
        if(ex instanceof ServiceException){
            return handleServiceException(exchange, (ServiceException)ex);
        }

        return Mono.error(ex);
    }

    @SneakyThrows
    private Mono<Void> handleParamException(ServerWebExchange exchange, ParamException ex) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        DataBufferFactory bufferFactory = response.bufferFactory();
        ObjectMapper objectMapper = new ObjectMapper();

        ResponseInfoVO responseInfoVO = new ResponseInfoVO();
        responseInfoVO.fail(416, ex.getLocalizedMessage());

        DataBuffer wrap = bufferFactory.wrap(objectMapper.writeValueAsBytes(responseInfoVO));
        return response.writeWith(Mono.fromSupplier(() -> wrap));
    }



    @SneakyThrows
    private Mono<Void> handleServiceException(ServerWebExchange exchange, ServiceException ex) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        DataBufferFactory bufferFactory = response.bufferFactory();
        ObjectMapper objectMapper = new ObjectMapper();

        ResponseInfoVO responseInfoVO = new ResponseInfoVO();
        responseInfoVO.fail(500, ex.getLocalizedMessage());

        DataBuffer wrap = bufferFactory.wrap(objectMapper.writeValueAsBytes(responseInfoVO));
        return response.writeWith(Mono.fromSupplier(() -> wrap));
    }

    private Mono<Void> handleResponseStatusException(ServerWebExchange exchange, ResponseStatusException ex) {
        exchange.getResponse().setStatusCode(ex.getStatusCode());
        return exchange.getResponse().setComplete();
    }
}