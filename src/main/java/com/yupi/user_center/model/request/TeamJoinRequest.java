package com.yupi.user_center.model.request;

import java.io.Serializable;
/**
 * @author leikooo
 * @create 2023-09-07 21:58
 * @Package com.yupi.user_center.model.request
 * @Description
 */
public class TeamJoinRequest implements Serializable {
    private static final long serialVersionUID = -4894468952406021507L;

    /**
     * id 队伍 id
     */
    private Long id;

    /**
     * 密码
     */
    private String password;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
