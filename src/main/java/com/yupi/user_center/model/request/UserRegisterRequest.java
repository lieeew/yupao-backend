package com.yupi.user_center.model.request;

import java.io.Serializable;

/**
 * @author leikooo
 * @create 2023-07-03 16:49
 * @Package com.yupi.user_center.model.domain.request
 * @Description 用户注册请求提
 */
public class UserRegisterRequest implements Serializable {
    private static final long serialVersionUID = -5663892868471135665L;
    private String userAccount;
    private String userPassword;
    private String checkPassword;
    private String planetCode;
    private String phone;
    private String email;
    private Integer gender;

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

    public String getCheckPassword() {
        return checkPassword;
    }

    public void setCheckPassword(String checkPassword) {
        this.checkPassword = checkPassword;
    }

    public String getPlanetCode() {
        return planetCode;
    }

    public void setPlanetCode(String planetCode) {
        this.planetCode = planetCode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }
}
