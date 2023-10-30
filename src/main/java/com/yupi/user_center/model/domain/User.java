package com.yupi.user_center.model.domain;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author leikooo
 * @TableName user
 */
@TableName(value = "user")
@Getter
@NoArgsConstructor
public class User implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 头像
     */
    private String avatarUrl;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 是否有效
     */
    private Integer userStatus;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 替换时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    /**
     * 用户角色 0-普通会员  1-管理员
     */
    private Integer userRole;

    /**
     * 星球编号
     */
    private String planetCode;

    /**
     * 标签
     */
    private String tags;

    /**
     * 个人简介
     */
    private String profile;

    @TableField(exist = false)
    private static final long serialVersionUID = 5125049559299850365L;

    public User(String userAccount, String avatarUrl, String userPassword, Integer userRole, String planetCode) {
        this.userAccount = userAccount;
        this.avatarUrl = avatarUrl;
        this.userPassword = userPassword;
        this.userRole = userRole;
        this.planetCode = planetCode;
    }

    public User(Long id, String username, String userAccount, String avatarUrl, Integer gender, String userPassword, String phone, String email, Integer userStatus, Date createTime, Date updateTime, Integer isDelete, Integer userRole, String planetCode, String tags, String profile) {
        this.id = id;
        this.username = username;
        this.userAccount = userAccount;
        this.avatarUrl = avatarUrl;
        this.gender = gender;
        this.userPassword = userPassword;
        this.phone = phone;
        this.email = email;
        this.userStatus = userStatus;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.isDelete = isDelete;
        this.userRole = userRole;
        this.planetCode = planetCode;
        this.tags = tags;
        this.profile = profile;
    }
}