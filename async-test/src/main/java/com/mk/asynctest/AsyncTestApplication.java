package com.mk.asynctest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class AsyncTestApplication extends SpringBootServletInitializer {
    private static final Logger logger = LoggerFactory.getLogger(AsyncTestApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(AsyncTestApplication.class, args);
        logger.debug("服务启动完成");
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(AsyncTestApplication.class);
    }

}
