package com.nageoffer.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nageoffer.shortlink.admin.dao.entity.GroupDo;
import com.nageoffer.shortlink.admin.dto.req.ShortLinkGroupSortReqDTO;
import com.nageoffer.shortlink.admin.dto.req.ShortLinkGroupUpdateReqDTO;
import com.nageoffer.shortlink.admin.dto.resq.ShortLinkGroupRespDTO;

import java.util.List;

/**
 * @author Juzi    2024/2/29 11:50
 * @version 1.0
 */
public interface GroupService extends IService<GroupDo> {
    void saveGroup(String groupName);

    List<ShortLinkGroupRespDTO> listGroup();
    void updateGroup(ShortLinkGroupUpdateReqDTO shortLinkGroupUpdateReqDTO);

    void deleteGroup(String gid);

    void sortGroup(List<ShortLinkGroupSortReqDTO> shortLinkGroupSortReqDTO);
}

