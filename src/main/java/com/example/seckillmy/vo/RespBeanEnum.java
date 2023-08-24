package com.example.seckillmy.vo;

public enum RespBeanEnum {

    SUCCESS(200,"SUCCESS"),
    ERROR(500, "服务端异常"),

    //登录模块
    LOGIN_ERROR(500210, "用户名或者密码不正确"),
    MOBILE_ERROR(500211, "手机号码格式不正确"),
    BIND_ERROR(500212, "参数校验异常"),
    MOBILE_NOT_EXIST(500213, "手机号码不存在"),
    PASSWORD_UPDATE_FAIL(500214, "更新密码失败"),
    SESSION_ERROR(500215, "用户SESSION不存在"),

    JWT_ERROR(444,"JWT失败"),
    EMPTY_MESS(404,"库中没有该商品信息"),
    UPDATE_FAIL(404,"更新值不存在"),
    //秒杀模块
    EMPTY_STOCK(500500, "库存不足"),
    REPEATE_ERROR(500501, "该商品每人限购一件"),
    REQUEST_ILLEGAL(500502, "请求非法，请重新尝试"),
    ERROR_CAPTCHA(500503, "验证码错误，请重新输入"),
    ACCESS_LIMIT_REACHED(500504, "访问过于频繁，请稍后重试"),
    //订单模块5003xx
    ORDER_NOT_EXIST(500300, "订单不存在");

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private Integer code;
    private String message;

    RespBeanEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
