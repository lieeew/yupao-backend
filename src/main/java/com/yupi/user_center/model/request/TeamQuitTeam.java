package com.yupi.user_center.model.request;

import java.io.Serializable;

/**
 * @author leikooo
 * @create 2023-09-12 19:42
 * @Package com.yupi.user_center.model.request
 * @Description 退出队伍需要传入的参数
 */
public class TeamQuitTeam implements Serializable {
    private static final long serialVersionUID = 2735122568326728702L;

    /**
     * 需要退出队伍的 id
     */
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
