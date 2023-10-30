package com.yupi.user_center.model.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.yupi.user_center.common.ErrorCode;
import com.yupi.user_center.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户队伍关系
 * @author leikooo
 * @TableName user_team
 */
@TableName(value ="user_team")
@Getter
@NoArgsConstructor
public class UserTeam implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 队伍id
     */
    private Long teamId;

    /**
     * 加入时间
     */
    private Date joinTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = -4803328770162900181L;

    public UserTeam(Long id, Long userId, Long teamId, Date joinTime, Date createTime, Date updateTime, Integer isDelete) {
        // 在这里校验非常的方便
        if (userId <= 0 || teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "teamId or userId is illegal");
        }
        this.id = id;
        this.userId = userId;
        this.teamId = teamId;
        this.joinTime = joinTime;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.isDelete = isDelete;
    }

    public UserTeam(Long userId, Long teamId) {
        if (userId <= 0 || teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "teamId or userId is illegal");
        }
        this.userId = userId;
        this.teamId = teamId;
    }
}