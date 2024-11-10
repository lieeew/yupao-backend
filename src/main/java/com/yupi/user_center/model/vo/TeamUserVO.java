package com.yupi.user_center.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author leikooo
 * @create 2023-09-08 20:58
 * @Package com.yupi.user_center.model.vo
 * @Description team 的包装类（脱敏）
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TeamUserVO implements Serializable {
    private static final long serialVersionUID = -6539018889531025460L;

    /**
     * id
     */
    private String id;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 用户id（队长 id）
     */
    private Long userId;

    /**
     * 当前人数
     */
    private Long memberCount;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 参加人用户信息
     */
    private List<UserVO> userList;

    /**
     * 创建人用户信息
     */
    private UserVO creatUser;

}
