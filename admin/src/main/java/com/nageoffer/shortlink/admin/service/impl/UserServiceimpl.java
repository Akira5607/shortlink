package com.nageoffer.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nageoffer.shortlink.admin.common.convention.exception.ClientException;
import com.nageoffer.shortlink.admin.common.enums.UserErrorCodeEnum;
import com.nageoffer.shortlink.admin.dao.entity.UserDo;
import com.nageoffer.shortlink.admin.dao.mapper.UserMapper;
import com.nageoffer.shortlink.admin.dto.req.UserLoginReqDTO;
import com.nageoffer.shortlink.admin.dto.req.UserRegisterReqDTO;
import com.nageoffer.shortlink.admin.dto.req.UserUpdateReqDTO;
import com.nageoffer.shortlink.admin.dto.resq.UserLoginResqDTO;
import com.nageoffer.shortlink.admin.dto.resq.UserResqDTO;
import com.nageoffer.shortlink.admin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.nageoffer.shortlink.admin.common.constant.RedisCache.LOCK_USER_REGISTER_KEY;
import static com.nageoffer.shortlink.admin.common.enums.UserErrorCodeEnum.*;

/**
 * @author Juzi    2024/2/28 14:25
 * @version 1.0
 * 用户接口实现层
 */
@Service
@RequiredArgsConstructor
public class UserServiceimpl extends ServiceImpl<UserMapper, UserDo> implements UserService {
    private final RBloomFilter<String> userRegisterCachePenetrationBloomFilter;
    private final RedissonClient redissonClient;
    private final StringRedisTemplate stringRedisTemplate;
    @Override
    public UserResqDTO getUserByUserName(String username) {
        LambdaQueryWrapper<UserDo> queryMapper = Wrappers.lambdaQuery(UserDo.class).eq(UserDo::getUsername, username);
        UserDo userDo = baseMapper.selectOne(queryMapper);
        if(userDo == null){
            throw new ClientException(UserErrorCodeEnum.USER_NULL);
        }
        UserResqDTO result = new UserResqDTO();
        BeanUtils.copyProperties(userDo, result);
        return result;
    }

    @Override
    public Boolean hasUserName(String username) {
        return !userRegisterCachePenetrationBloomFilter.contains(username);
    }

    @Override
    public void update(UserUpdateReqDTO userUpdateReqDTO) {
        //TODO 验证是否是本人操作
        LambdaUpdateWrapper<UserDo> res = Wrappers.lambdaUpdate(UserDo.class)
                .eq(UserDo::getUsername, userUpdateReqDTO.getUsername());
        baseMapper.update(BeanUtil.toBean(userUpdateReqDTO, UserDo.class), res);
    }

    @Override
    public void register(UserRegisterReqDTO requestParam) {
        if(!hasUserName(requestParam.getUsername())){
            throw  new ClientException(USER_EXIST);
        }
        RLock lock = redissonClient.getLock(LOCK_USER_REGISTER_KEY+requestParam.getUsername());
        try {
            if (lock.tryLock()) {
                int inserted = baseMapper.insert(BeanUtil.toBean(requestParam, UserDo.class));
                if(inserted < 1){
                    throw new ClientException(USER_SAVE_ERROR);
                }
                userRegisterCachePenetrationBloomFilter.add(requestParam.getUsername());
                return;
            }
            throw new ClientException(USER_EXIST);
        } finally {
            lock.unlock();
        }

    }

    @Override
    public UserLoginResqDTO login(UserLoginReqDTO userLoginReqDTO) {
        LambdaQueryWrapper<UserDo> ans = Wrappers.lambdaQuery(UserDo.class)
                .eq(UserDo::getUsername, userLoginReqDTO.getUsername())
                .eq(UserDo::getPassword, userLoginReqDTO.getPassword())
                .eq(UserDo::getDelFlag, 0);
        UserDo userDo = baseMapper.selectOne(ans);
        if(userDo == null){
            throw new ClientException(USER_NULL);
        }
        if(Boolean.TRUE.equals(stringRedisTemplate.hasKey("login_" + userLoginReqDTO.getUsername()))){
            throw new ClientException(USER_LOGIN);
        }
        String uuid = UUID.randomUUID().toString();
        stringRedisTemplate.opsForHash().
                put("login_"+userLoginReqDTO.getUsername(),uuid,JSON.toJSONString(userDo));
        stringRedisTemplate.expire("login_"+userLoginReqDTO.getUsername(), 30L, TimeUnit.DAYS);
        return new UserLoginResqDTO(uuid);
    }

    @Override
    public Boolean checkLogin(String username,String token) {
        return stringRedisTemplate.opsForHash().get("login_"+username, token) != null;
    }

    @Override
    public void logOut(String username, String token) {
        if(!checkLogin(username, token)){
            throw new ClientException(USER_LOGIN_OUT);
        }
        stringRedisTemplate.opsForHash().delete("login_"+username, token);
    }
}
