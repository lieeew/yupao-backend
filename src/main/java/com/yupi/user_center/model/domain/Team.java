package com.yupi.user_center.model.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

/**
 * 队伍
 *
 * @TableName team
 */
@TableName(value = "team")
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
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    public Long getId() {
        return id;
    }

    /**
     * id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 队伍名称
     */
    public String getName() {
        return name;
    }

    /**
     * 队伍名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 最大人数
     */
    public Integer getMaxNum() {
        return maxNum;
    }

    /**
     * 最大人数
     */
    public void setMaxNum(Integer maxNum) {
        this.maxNum = maxNum;
    }

    /**
     * 过期时间
     */
    public Date getExpireTime() {
        return expireTime;
    }

    /**
     * 过期时间
     */
    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    /**
     * 用户id（队长 id）
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 用户id（队长 id）
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 密码
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     *
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     *
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Long getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(Long memberCount) {
        this.memberCount = memberCount;
    }

    public Long getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(Long isDelete) {
        this.isDelete = isDelete;
    }

}