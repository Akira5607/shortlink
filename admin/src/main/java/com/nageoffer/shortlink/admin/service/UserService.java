package com.nageoffer.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nageoffer.shortlink.admin.dao.entity.UserDo;
import com.nageoffer.shortlink.admin.dto.req.UserRegisterReqDTO;
import com.nageoffer.shortlink.admin.dto.resq.UserResqDTO;

/**
 * @author Juzi    2024/2/28 14:25
 * @version 1.0
 */
public interface UserService extends IService<UserDo> {
    /*
    * 根据用户名返回实体*/

    UserResqDTO getUserByUserName(String username);
    Boolean hasUserName(String username);
    void register(UserRegisterReqDTO requestParam);
}
