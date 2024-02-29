package com.nageoffer.shortlink.admin.dto.resq;

import lombok.Data;

/**
 * @author Juzi    2024/2/29 13:48
 * @version 1.0
 */

@Data
public class ShortLinkGroupRespDTO {
    /**
     * 分组标识
     */
    private String gid;

    /**
     * 分组名称
     */
    private String name;

    /**
     * 分组排序
     */
    private Integer sortOrder;

    /**
     * 分组下短链接数量
     */
    private Integer shortLinkCount;
}
