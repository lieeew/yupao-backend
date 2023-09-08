package com.yupi.user_center.model.request;

import java.io.Serializable;

/**
 * @author leikooo
 * @create 2023-07-03 17:01
 * @Package com.yupi.user_center.model.domain.request
 * @Description
 */

public class UserLoginRequest implements Serializable {
    private static final long serialVersionUID = -3401357311924778926L;
    private String userAccount;
    private String userPassword;

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }
}
