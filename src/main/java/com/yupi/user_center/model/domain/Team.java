package com.yupi.user_center.model.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.yupi.user_center.common.ErrorCode;
import com.yupi.user_center.exception.BusinessException;
import com.yupi.user_center.model.dto.TeamQuery;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.beanutils.BeanUtils;

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
    @TableField("id")
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 队伍名称
     */
    @TableField("name")
    private String name;

    /**
     * 描述
     */
    @TableField("description")
    private String description;

    /**
     * 最大人数
     */
    @TableField("maxNum")
    private Integer maxNum;

    /**
     * 过期时间
     */
    @TableField("expireTime")
    private Date expireTime;

    /**
     * 用户id（队长 id）
     */
    @TableField("userId")
    private Long userId;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    @TableField("status")
    private Integer status;

    /**
     * 密码
     */
    @TableField("password")
    private String password;

    /**
     * 创建时间
     */
    @TableField("createTime")
    private Date createTime;

    /**
     *
     */
    @TableField("updateTime")
    private Date updateTime;

    /**
     * 加入队伍的人数
     */
    @TableField("memberCount")
    private Long memberCount;

    /**
     * 是否删除
     */
    @TableLogic
    @TableField("isDelete")
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