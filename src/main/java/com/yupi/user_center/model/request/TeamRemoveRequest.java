package com.yupi.user_center.model.request;

import java.io.Serializable;

/**
 * @author leikooo
 * @create 2023-09-07 21:58
 * @Package com.yupi.user_center.model.request
 * @Description 删除队伍传入的参数
 */
public class TeamRemoveRequest implements Serializable {

    private static final long serialVersionUID = -43284998019872033L;

    /**
     * 需要删除队伍的 id
     */
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
