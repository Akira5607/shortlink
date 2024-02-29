package com.nageoffer.shortlink.admin.common.enums;

import com.nageoffer.shortlink.admin.common.convention.errorcode.IErrorCode;

/**
 * @author Juzi    2024/2/28 15:28
 * @version 1.0
 */
public enum UserErrorCodeEnum implements IErrorCode {


    USER_NULL("b000200", "用户不存在"),
    USER_SAVE_ERROR("b000202", "用户新增失败"),
    USER_EXIST("b000201", "用户存在"),
    USER_LOGIN("b000204", "用户已经登录"),
    USER_LOGIN_OUT("b000205", "用户未登录");

    private final String code;

    private final String message;

    UserErrorCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
