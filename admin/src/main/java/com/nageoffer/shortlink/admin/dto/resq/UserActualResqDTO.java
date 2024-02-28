package com.nageoffer.shortlink.admin.dto.resq;

import lombok.Data;

/**
 * @author Juzi    2024/2/28 14:32
 * @version 1.0
 */
@Data
public class UserActualResqDTO {
    /**
     * id
     */
    private Long id;
    /**
     * 用户名
     */
    private String username;
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
