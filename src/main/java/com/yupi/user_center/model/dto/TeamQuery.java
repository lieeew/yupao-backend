package com.yupi.user_center.model.dto;

import com.yupi.user_center.common.PageRequest;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author leikooo
 * @create 2023-09-06 21:06
 * @Package com.yupi.user_center.model.dto
 * @Description dto
 * 为什么需要新的类？
 * 1） 请求参数名称和实体类不一致
 * 2） 有一些参数使用不到，可以降低理解成本
 * 3) 多个对象映射到类里面
 */
public class TeamQuery extends PageRequest {
    /**
     * id
     */
    private Long id;

    /**
     *  需要查询的 id 序列
     */
    private List<Long> listIds;

    /**
     * 搜索关键词（同时搜索名称、描述）
     */
    private String searchText;
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
     * 用户id（队长 id）
     */
    private Long userId;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;
    /**
     * 当前页数
     */
    private int current = 1;

    /**
     * 该页的显示的数量
     */
    private int size = 10;

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getMaxNum() {
        return maxNum;
    }

    public void setMaxNum(Integer maxNum) {
        this.maxNum = maxNum;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<Long> getListIds() {
        return listIds;
    }

    public void setListIds(List<Long> listIds) {
        this.listIds = listIds;
    }
}
