package com.nageoffer.shortlink.admin.dto.req;

import lombok.Data;

/**
 * @author Juzi    2024/2/29 13:35
 * @version 1.0
 */
@Data
public class ShortLinkGroupSortReqDTO {
    private String gid;
    /**
     * 分组名
     */
    private Integer sortOrder;
}
