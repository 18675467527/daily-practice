package com.mk.webfluxtest.common;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.mk.webfluxtest.exception.ParamException;
import com.mk.webfluxtest.exception.ServiceException;
import com.mk.webfluxtest.utils.JackJsonUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.Date;
import java.util.Objects;

/**
 * @Author hjm
 * @Date 2024/11/20 14:43
 */
@Getter
@Setter
public class ResponseInfoVO<T> implements Serializable {
    // 创建时间
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date responseTime;
    // 响应状态码
    private String status;
    // 响应状态码
    private String sourceStatus;
    // 状态提示信息
    private String message;
    // 状态提示信息
    private String mac;
    // 错误信息
    private String errorInfoDesc;
    // 返回数据
    private T data;
    /**
     * 数据来源平台
     */
    private String sourcePlatform;


    public ResponseInfoVO() {
       //
    }

    public void success(T data) {
        this.status = ReturnResultEnum.STATUS_SUCCESS.getStatus();
        this.sourceStatus = ReturnResultEnum.STATUS_SUCCESS.getStatus();
        this.message = ReturnResultEnum.STATUS_SUCCESS.getMessage();
        this.errorInfoDesc = null;
        this.responseTime = new Date();
        this.data = data;
        this.mac = Objects.isNull(data) ? null : sha256Hex(JackJsonUtil.jsonToString(data));
    }

    public void fail(Integer errorHttpCode, String errorInfoDesc) {
        ReturnResultEnum resultEnum = ReturnResultEnum.STATUS_FAIL_500;
        ReturnResultEnum returnResultEnum = ReturnResultEnum.get(errorHttpCode);
        if (Objects.nonNull(returnResultEnum)) {
            resultEnum = returnResultEnum;
        }
        fail(resultEnum, errorInfoDesc);
    }

    public void fail(Exception e, String errorInfoDesc) {
        ReturnResultEnum resultEnum = ReturnResultEnum.STATUS_FAIL_500;
        if (e instanceof SQLException) {
            resultEnum = ReturnResultEnum.STATUS_FAIL_SIX;
        } else if (e instanceof ParamException
                || e instanceof MethodArgumentNotValidException
                || e instanceof BindException) {
            resultEnum = ReturnResultEnum.STATUS_FAIL_TWO;
        } else if (e instanceof ServiceException) {
            resultEnum = ReturnResultEnum.STATUS_FAIL_SEVEN;
        }
        fail(resultEnum, errorInfoDesc);
    }

    public void fail(ReturnResultEnum resultEnum, String errorInfoDesc) {
        this.status = resultEnum.getStatus();
        this.sourceStatus = resultEnum.getStatus();
        this.responseTime = new Date();
        this.message = resultEnum.getMessage();
        this.errorInfoDesc = errorInfoDesc;
    }

    private String sha256Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating SHA-256 hash", e);
        }
    }
}

