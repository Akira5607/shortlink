package com.nageoffer.shortlink.admin.dto.req;

import lombok.Data;

/**
 * @author Juzi    2024/2/29 10:07
 * @version 1.0
 */
@Data
public class UserLoginReqDTO {
    private String username;
    private String password;
}
