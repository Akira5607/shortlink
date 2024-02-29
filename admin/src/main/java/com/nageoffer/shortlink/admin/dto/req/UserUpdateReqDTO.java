package com.nageoffer.shortlink.admin.dto.req;

import lombok.Data;

/**
 * @author Juzi    2024/2/29 9:52
 * @version 1.0
 */
@Data
public class UserUpdateReqDTO {
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String mail;
}
