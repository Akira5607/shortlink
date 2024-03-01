package com.nageoffer.shortlink.admin.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nageoffer.shortlink.admin.common.biz.user.UserContext;
import com.nageoffer.shortlink.admin.common.convention.result.Result;
import com.nageoffer.shortlink.admin.dao.entity.GroupDo;
import com.nageoffer.shortlink.admin.dao.mapper.GroupMapper;
import com.nageoffer.shortlink.admin.dto.req.ShortLinkGroupSortReqDTO;
import com.nageoffer.shortlink.admin.dto.req.ShortLinkGroupUpdateReqDTO;
import com.nageoffer.shortlink.admin.dto.resq.ShortLinkGroupRespDTO;
import com.nageoffer.shortlink.admin.remote.dto.ShortLinkRemoteService;
import com.nageoffer.shortlink.admin.remote.dto.resp.ShortLinkGroupCountQueryRespDTO;
import com.nageoffer.shortlink.admin.service.GroupService;
import com.nageoffer.shortlink.admin.toolkit.RandomGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Juzi    2024/2/29 11:50
 * @version 1.0
 */
@Slf4j
@Service
public class GroupServiceImpl extends ServiceImpl<GroupMapper, GroupDo> implements GroupService {
    ShortLinkRemoteService shortLinkRemoteService = new ShortLinkRemoteService(){};
    @Override
    public void saveGroup(String groupName) {
        saveGroup(UserContext.getUsername(), groupName);
    }

    @Override
    public void saveGroup(String username, String groupName) {
        String gid;
        do {
            gid = RandomGenerator.generateRandom();
        } while (!hasGid(username, gid));
        GroupDo build = GroupDo.builder()
                .gid(gid)
                .sortOrder(0)
                .username(username)
                .name(groupName)
                .build();
        baseMapper.insert(build);
    }

    private boolean hasGid(String username, String gid){
        LambdaQueryWrapper<GroupDo> queryWrapper = Wrappers.lambdaQuery(GroupDo.class)
                .eq(GroupDo::getGid, gid)
                .eq(GroupDo::getUsername, Optional.ofNullable(username).orElse(UserContext.getUsername()));
        GroupDo groupDo = baseMapper.selectOne(queryWrapper);
        return groupDo == null;
    }

    @Override
    public List<ShortLinkGroupRespDTO> listGroup() {
        LambdaQueryWrapper<GroupDo> queryWrapper = Wrappers.lambdaQuery(GroupDo.class)
                .eq(GroupDo::getDelFlag, 0)
                .eq(GroupDo::getUsername, UserContext.getUsername())
                .orderByDesc(GroupDo::getSortOrder, GroupDo::getUpdateTime);
        List<GroupDo> groupDos = baseMapper.selectList(queryWrapper);
        Result<List<ShortLinkGroupCountQueryRespDTO>> listResult = shortLinkRemoteService
                .listGroupShortLinkCount(groupDos.stream().map(GroupDo::getGid).toList());
        List<ShortLinkGroupRespDTO> shortLinkGroupResp = BeanUtil.copyToList(groupDos, ShortLinkGroupRespDTO.class);
        shortLinkGroupResp.forEach(each->{
            Optional<ShortLinkGroupCountQueryRespDTO> first = listResult.getData().stream()
                    .filter(item -> Objects.equals(item.getGid(), each.getGid()))
                    .findFirst();
            first.ifPresent(item-> each.setShortLinkCount(first.get().getShortLinkCount()));
        });
        return shortLinkGroupResp;
    }

    @Override
    public void updateGroup(ShortLinkGroupUpdateReqDTO shortLinkGroupUpdateReqDTO) {
        LambdaUpdateWrapper<GroupDo> queryWrapper = Wrappers.lambdaUpdate(GroupDo.class)
                .eq(GroupDo::getUsername, UserContext.getUsername())
                .eq(GroupDo::getGid, shortLinkGroupUpdateReqDTO.getGid())
                .eq(GroupDo::getDelFlag, 0);
        GroupDo groupDo = new GroupDo();
        groupDo.setName(shortLinkGroupUpdateReqDTO.getName());
        baseMapper.update(groupDo, queryWrapper);
    }

    @Override
    public void deleteGroup(String gid) {
        LambdaUpdateWrapper<GroupDo> queryWrapper = Wrappers.lambdaUpdate(GroupDo.class)
                .eq(GroupDo::getUsername, UserContext.getUsername())
                .eq(GroupDo::getGid, gid)
                .eq(GroupDo::getDelFlag, 0);
        GroupDo groupDo = new GroupDo();
        groupDo.setDelFlag(1);
        baseMapper.update(groupDo, queryWrapper);
    }

    @Override
    public void sortGroup(List<ShortLinkGroupSortReqDTO> shortLinkGroupSortReqDTO) {
        shortLinkGroupSortReqDTO.forEach(each ->{
            GroupDo build = GroupDo.builder()
                    .sortOrder(each.getSortOrder())
                    .build();
            LambdaUpdateWrapper<GroupDo> eq = Wrappers.lambdaUpdate(GroupDo.class)
                    .eq(GroupDo::getUsername, UserContext.getUsername())
                    .eq(GroupDo::getGid, each.getGid())
                    .eq(GroupDo::getDelFlag, 0);
            baseMapper.update(build, eq);
        });
    }


}
