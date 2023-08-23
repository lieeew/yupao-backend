package com.yupi.user_center.service;

import com.yupi.user_center.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author liang
 * @description 针对表【user】的数据库操作Service
 * @createDate 2023-07-02 16:09:52
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册方法
     *
     * @param userAccount   用户账号
     * @param userPassword  用户密码
     * @param checkPassword 第二次密码
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode);

    /**
     * 用户登录
     *
     * @param userAccount  用户账号
     * @param userPassword 用户密码
     * @return 返回脱敏后的用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 得到脱敏的用户
     * @param originUser 脱敏前的原始用户
     * @return 脱敏后的用户
     */
    User getSafetyUser(User originUser);

    /**
     * 用户注销
     */
    int userLogout(HttpServletRequest request);

    /**
     * 通过标签查询用户
     *
     * @param tagNameList 标签
     * @return
     */
    List<User> searchUserByTagsBySQL(List<String> tagNameList);

    /**
     * 使用内存方式查询 user
     *
     * @param tagNameList 需要有的 user
     * @return
     */
    List<User> searchUserByTagsByMemory(List<String> tagNameList);

    /**
     * 更新用户信息
     *
     * @param user
     * @return
     */
    int updateUser (User user, User loginUser);

    /**
     * 获取当前的用用户信息
     *
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 进行鉴权，仅管理员可以查询
     *
     * @param request request 请求
     * @return true-管理员   false-代表普通用户
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 对健全方法进行重载
     *
     * @param loginUser
     * @return
     */
    boolean isAdmin(User loginUser);
}
