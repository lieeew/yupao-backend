package com.yupi.user_center.model.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 队伍
 *
 * @author leikooo
 * @TableName team
 */
@TableName(value = "team")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Team implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
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
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;

    /**
     * 密码
     */
    private String password;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     *
     */
    private Date updateTime;

    /**
     * 加入队伍的人数
     */
    private Long memberCount;

    /**
     * 是否删除
     */
    @TableLogic
    private Long isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = -8005753153332247283L;

    /**
     * 修改队伍人数
     *
     * @param oldTeam
     * @param count
     * @return
     */
    public Team changeMemberCount(final Team oldTeam, final long count) {
        return new Team(oldTeam.getId(), oldTeam.getName(), oldTeam.getDescription(), oldTeam.getMaxNum(), oldTeam.getExpireTime(), oldTeam.getUserId(), oldTeam.getStatus(), oldTeam.getPassword(), oldTeam.getCreateTime(), oldTeam.getUpdateTime(), count, oldTeam.getIsDelete());
    }
}