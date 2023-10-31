package com.yupi.user_center.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @author leikooo
 * @create 2023-09-07 21:58
 * @Package com.yupi.user_center.model.request
 * @Description
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TeamUpdateRequest implements Serializable {

    private static final long serialVersionUID = 5649862910201151941L;
    /**
     * id 队伍 id
     */
    private Long id;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;

    /**
     * 密码
     */
    private String password;
}
