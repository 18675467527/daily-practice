package com.mk.webfluxtest.exception;

/**
 * @Author hjm
 * @Date 2024/11/20 14:34
 */
public class ServiceException extends RuntimeException {
    public ServiceException(String msg) {
        super(msg);
    }
}
