package com.nageoffer.shortlink.admin.controller;

import cn.hutool.core.bean.BeanUtil;
import com.nageoffer.shortlink.admin.common.convention.result.Result;
import com.nageoffer.shortlink.admin.common.convention.result.Results;
import com.nageoffer.shortlink.admin.dto.req.UserLoginReqDTO;
import com.nageoffer.shortlink.admin.dto.req.UserRegisterReqDTO;
import com.nageoffer.shortlink.admin.dto.req.UserUpdateReqDTO;
import com.nageoffer.shortlink.admin.dto.resq.UserActualResqDTO;
import com.nageoffer.shortlink.admin.dto.resq.UserLoginResqDTO;
import com.nageoffer.shortlink.admin.dto.resq.UserResqDTO;
import com.nageoffer.shortlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RedissonClient;
import org.springframework.web.bind.annotation.*;

/**
 * @author Juzi    2024/2/28 13:13
 * @version 1.0
 * 用户管理控制层
 */
@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final RedissonClient redissonClient;

    @GetMapping("/api/short-link/admin/v1/user/{username}")
    public Result<UserResqDTO> getUserByUsername(@PathVariable("username") String username){
        UserResqDTO res = userService.getUserByUserName(username);

            return Results.success(res);
    }
    @GetMapping("/api/short-link/admin/v1/actual/user/{username}")
    public Result<UserActualResqDTO> getActualUserByUsername(@PathVariable("username") String username){
        return Results.success(BeanUtil.toBean(userService.getUserByUserName(username),UserActualResqDTO.class));
    }
    @GetMapping("/api/short-link/admin/v1/user/has-username")
    public Result<Boolean> hasUserName(@RequestParam("username") String username){
        return Results.success(userService.hasUserName(username));
    }
    @PostMapping("/api/short-link/admin/v1/user")
    public Result<Void> register(@RequestBody UserRegisterReqDTO userRegisterReqDTO){
        userService.register(userRegisterReqDTO);
        return Results.success();
    }
    @PutMapping("/api/short-link/admin/v1/user")
    public Result<Void> update(@RequestBody UserUpdateReqDTO userUpdateReqDTO){
        userService.update(userUpdateReqDTO);
        return Results.success();
    }
    @PostMapping("/api/short-link/admin/v1/user/login")
    public Result<UserLoginResqDTO> login(@RequestBody UserLoginReqDTO userLoginReqDTO){
        UserLoginResqDTO res = userService.login(userLoginReqDTO);
        return Results.success(res);
    }
    @GetMapping("/api/short-link/admin/v1/user/check-login")
    public Result<Boolean> checkLogin(@RequestParam("username")String username,@RequestParam("token") String token){
        return Results.success(userService.checkLogin(username, token));
    }
    @DeleteMapping("/api/short-link/admin/v1/user/logout")
    public Result<Void> logOut(@RequestParam("username")String username,@RequestParam("token") String token){
        userService.logOut(username, token);
        return Results.success();
    }
}
