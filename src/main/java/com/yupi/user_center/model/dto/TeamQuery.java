package com.yupi.user_center.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

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
@Getter
@NoArgsConstructor
public class TeamQuery {
    /**
     * id
     */
    private Long id;

    /**
     * 需要查询的 id 序列
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
     * pageNum
     */
    private int pageNum = 1;

    /**
     * pageSize
     */
    private int pageSize = 10;

    public TeamQuery(Long id, List<Long> listIds, String searchText, String name, String description, Integer maxNum, Long userId, Integer status, int pageNum, int pageSize) {
        this.id = id;
        this.listIds = listIds;
        this.searchText = searchText;
        this.name = name;
        this.description = description;
        this.maxNum = maxNum;
        this.userId = userId;
        this.status = status;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
    }

    /**
     * 修改 idLists
     *
     * @param originalObj
     * @param listIds
     * @return
     */
    public TeamQuery changeIdLists(final TeamQuery originalObj, final List<Long> listIds) {
        return new TeamQuery(originalObj.getId(), listIds, originalObj.getSearchText(), originalObj.getName(), originalObj.getDescription(), originalObj.getMaxNum(), originalObj.getUserId(), originalObj.getStatus(), originalObj.getPageNum(), originalObj.getPageSize());
    }

    /**
     * @param originalObj
     * @param userId      队长 id
     *                    * @return
     */
    public TeamQuery changeUserId(final TeamQuery originalObj, final Long userId) {
        return new TeamQuery(originalObj.getId(), originalObj.getListIds(), originalObj.getSearchText(), originalObj.getName(), originalObj.getDescription(), originalObj.getMaxNum(), userId, originalObj.getStatus(), originalObj.getPageNum(), originalObj.getPageSize());
    }
}
