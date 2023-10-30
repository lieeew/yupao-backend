package com.yupi.user_center.model.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

/**
 * 标签表
 * @author leikooo
 * @TableName tag
 */
@TableName(value ="tag")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Tag implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 标签名称
     */
    private String tagName;

    /**
     * 用户 id
     */
    private String userId;

    /**
     * 父标签
     */
    private Long parentId;

    /**
     * 0-是父标签, 1-不是父标签
     */
    private Integer isParent;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     * 必须要加上逻辑删除啊的注解 @TableLogic
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 4670705451939200703L;

  }