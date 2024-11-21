package com.mk.webfluxtest.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mk.webfluxtest.common.ResponseInfoVO;
import com.mk.webfluxtest.common.ReturnResultEnum;
import com.mk.webfluxtest.utils.JackJsonUtil;
import io.micrometer.common.util.StringUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @Author hjm
 * @Date 2024/11/20 15:22
 * 返回数据统一处理filter
 */
@Slf4j
@Component
public class ResponseBodyFilter implements WebFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String requestUrl = request.getPath().toString();
        log.info(requestUrl + "--requestUrl");
        ServerHttpResponse originalResponse = exchange.getResponse();
        DataBufferFactory bufferFactory = originalResponse.bufferFactory();
        long currentTimeMillis = System.currentTimeMillis();

        ServerHttpResponseDecorator response = new ServerHttpResponseDecorator(originalResponse) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                Flux<? extends DataBuffer> fluxBody = Flux.from(body);
                //（返回数据内如果字符串过大，默认会切割）解决返回体分段传输
                return super.writeWith(fluxBody.buffer().map(dataBuffers -> {
                    //如果响应过大，会进行截断，出现乱码，然后看api DefaultDataBufferFactory有个join方法可以合并所有的流，乱码的问题解决
                    DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
                    DataBuffer join = dataBufferFactory.join(dataBuffers);
                    byte[] content = new byte[join.readableByteCount()];
                    join.read(content);
                    //释放掉内存
                    DataBufferUtils.release(join);
                    HttpStatusCode statusCode = originalResponse.getStatusCode();
                    String responseData = new String(content, StandardCharsets.UTF_8);
                    ResponseInfoVO vo = new ResponseInfoVO();
                    try {
                        vo = JackJsonUtil.jsonToObject(responseData, ResponseInfoVO.class);
                    }catch (Exception e){
                    }

                    ResponseInfoVO responseInfoVo = new ResponseInfoVO();
                    String newResponseData = null;
                    if (statusCode.value() == 200 && null!= vo && null != vo.getStatus()) {
                        newResponseData = responseData;
                    }else if(statusCode.value() == 200 && (null== vo || null == vo.getStatus())){
                        responseInfoVo.success(responseData);
                        newResponseData = JackJsonUtil.jsonToStringAlways(responseInfoVo);
                    }else{
                        newResponseData = responseData;
                    }
                    byte[] uppedContent = new String(newResponseData.getBytes(), StandardCharsets.UTF_8).getBytes();
                    log.info("total请求耗时cost:{}ms", System.currentTimeMillis() - currentTimeMillis);
                    originalResponse.getHeaders().setContentLength(uppedContent.length);
                    return bufferFactory.wrap(uppedContent);
                }));
            }

            @Override
            public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
                return writeWith(Flux.from(body).flatMapSequential(p -> p));
            }
        };
        return chain.filter(exchange.mutate().response(response).build()).onErrorResume(e -> {
            ResponseInfoVO<Object> responseInfoVo = new ResponseInfoVO<>();
            responseInfoVo.setMac(null);
            responseInfoVo.setData(null);
            responseInfoVo.fail(ReturnResultEnum.STATUS_FAIL_500, "接口调用失败：" + e.getLocalizedMessage());
            return getExceptionResult(response, responseInfoVo);
        });
    }

    @SneakyThrows
    private Mono<Void> getExceptionResult(ServerHttpResponse response, ResponseInfoVO responseInfoVO) {
        response.setStatusCode(HttpStatus.OK);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        DataBufferFactory bufferFactory = response.bufferFactory();
        ObjectMapper objectMapper = new ObjectMapper();
        DataBuffer wrap = bufferFactory.wrap(objectMapper.writeValueAsBytes(responseInfoVO));
        return response.writeWith(Mono.fromSupplier(() -> wrap));
    }


    @Override
    public int getOrder() {
        return 10;
    }
}
