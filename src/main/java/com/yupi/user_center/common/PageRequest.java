package com.yupi.user_center.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author leikooo
 * @create 2023-09-06 21:35
 * @Package com.yupi.user_center.model.dto
 * @Description
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PageRequest implements Serializable {
    private static final long serialVersionUID = -7354124501767087987L;
    /**
     * 页面大小
     */
    protected int pageSize = 10;

    /**
     * 当前是第几页
     */
    protected int pageNum = 1;

}
