package com.mk.webfluxtest.common;

/**
 * 响应值枚举
 * status：系统内部自定义异常编码
 * message：系统内部自定义异常名称
 * httpCode：标准http协议响应编码（用于拦截自定义异常的响应值）
 * 注意：如果你需要新增一个状态编码，请在com.mk.exception.DatacenterExceptionHandler中配置相应的异常拦截操作，这样才能得到预期的响应
 */
public enum ReturnResultEnum {
    STATUS_SUCCESS("0", "返回成功", 200),
    STATUS_FAIL_ONE("-1", "Token异常", 401),
    STATUS_FAIL_TWO("-2", "参数异常", 400),
    STATUS_FAIL_THREE("-3", "无接口权限", 403),
    STATUS_FAIL_FOUR("-4", "访问频率过高，系统拒绝访问", 429),
    STATUS_FAIL_FIVE("-5", "访问IP受限", 407),
    STATUS_FAIL_SIX("-6", "内部数据库错误", 510),
    STATUS_FAIL_SEVEN("-7", "业务异常", 501),
    STATUS_FAIL_EIGHT("-8", "参数类型非法", 417),
    //STATUS_FAIL_NINETY_NINE("-99", "其它错误", 500),
    STATUS_FAIL_NINE_HUNDRED_AND_NINETY_NINE("-999","业务数据不存在", 412),
    STATUS_FAIL_416("416","参数类型非法", 416),
    STATUS_FAIL_500("500","业务数据不存在", 500),
    ;
    private String status;
    private String message;
    private Integer httpCode;

    ReturnResultEnum(String status, String message, Integer httpCode) {
        this.status = status;
        this.message = message;
        this.httpCode = httpCode;
    }

    public String getStatus() {
        return status;
    }

    public Integer intStatus() {
        return Integer.parseInt(status);
    }

    public String getMessage() {
        return message;
    }

    public Integer getHttpCode() {
        return httpCode;
    }

    public static ReturnResultEnum get(Integer httpCode) {
        ReturnResultEnum[] scheduledStatusEnums = values();
        for (ReturnResultEnum scheduledStatusEnum : scheduledStatusEnums) {
            if (scheduledStatusEnum.getHttpCode().equals(httpCode)) {
                return scheduledStatusEnum;
            }
        }
        return null;
    }

}
