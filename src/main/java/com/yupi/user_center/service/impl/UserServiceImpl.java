package com.yupi.user_center.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yupi.user_center.common.ErrorCode;
import com.yupi.user_center.exception.BusinessException;
import com.yupi.user_center.model.domain.User;
import com.yupi.user_center.service.UserService;
import com.yupi.user_center.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.yupi.user_center.contant.UserConstant.ADMIN_ROLE;
import static com.yupi.user_center.contant.UserConstant.USER_LOGIN_STATE;

/**
 * 写道 service 里面的方法是一般可能会「服用」的代码
 * @author leikooo
 * @description 针对表【user】的数据库操作Service实现
 * @createDate 2023-07-02 16:09:52
 */
@Service
@Slf4j // 可以加日志
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserMapper userMapper;
    /**
     * 盐值
     * 快捷键 prsf
     */
    private static final String SALT = "yupi";


    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode) {
        // 一、 校验, 需要非空
        if (StringUtils.isAllBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 二、 校验用户的账户、密码、校验密码，是否符合要求
        // 2. 账户长度不小于 4 位
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号长度小于 4 位");
        }
        // 3. 密码就不小于 8 位
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码小于 8 位");
        }
        // 4. 账户不包含特殊字符
        String pattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(pattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "包含特殊字符");
        }

        // 5. 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码和校验密码不一致");
        }

        // 6. 账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "已经存在该用户");
        }

        // 7. 星球编号现在设置为不大于 5 位
        if (planetCode.length() > 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "星球参数大于 5 位");
        }

        // 7. 星球编号不能重复
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("planetCode", planetCode);
        count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "星球编号不能重复");
        }


        // 三、 插入数据
        // 对密码进行加盐
        User user = new User();

        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setPlanetCode(planetCode);
        // 设置默认图片的路径
        user.setAvatarUrl("https://zos.alipayobjects.com/rmsportal/jkjgkEfvpUPVyRjUImniVslZfWPnJuuZ.png");
        // 保存用户
        boolean save = this.save(user);
        if (!save) {
            throw new BusinessException(ErrorCode.SAVE_FAILURE);
        }
        // 返回注册的 ID 号
        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 一、 校验, 需要非空
        if (StringUtils.isAllBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 二、 校验用户的账户、密码、校验密码，是否符合要求
        // 2. 账户长度不小于 4 位
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号长度小于 4 位");
        }
        // 3. 密码就不小于 8 位
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码小于 8 位");
        }
        // 4. 账户不包含特殊字符
        String pattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(pattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "包含特殊字符");
        }

        // 三、 检查数据
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询数据库, 进行比对密码的操作
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            // 记录日志尽量使用英文，不容易出现乱码
            log.info("user login failed can not match password");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号或者密码错误");
        }

        // 这里重新生成一个对象，进行返回数据

        User saftyUser = getSafetyUser(user);

        // 记录用户的登录状态
        request.getSession().setAttribute(USER_LOGIN_STATE, saftyUser);

        return saftyUser;
    }

    /**
     * 得到脱敏的用户
     *
     * @param originUser 脱敏前的原始用户
     * @return 脱敏后的用户
     */
    @Override
    public User getSafetyUser(User originUser) {
        // service 也需要校验的
        if (originUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 脱敏
        User saftyUser = new User();

        saftyUser.setId(originUser.getId());
        saftyUser.setUsername(originUser.getUsername());
        saftyUser.setUserAccount(originUser.getUserAccount());
        saftyUser.setAvatarUrl(originUser.getAvatarUrl());
        saftyUser.setGender(originUser.getGender());
        saftyUser.setPhone(originUser.getPhone());
        saftyUser.setEmail(originUser.getEmail());
        saftyUser.setUserStatus(originUser.getUserStatus());
        saftyUser.setCreateTime(originUser.getCreateTime());
        saftyUser.setIsDelete(originUser.getIsDelete());
        saftyUser.setUserRole(originUser.getUserRole());
        saftyUser.setPlanetCode(originUser.getPlanetCode());
        saftyUser.setTags(originUser.getTags());
        saftyUser.setProfile(originUser.getProfile());
        return saftyUser;
    }

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    @Override
    public int userLogout(HttpServletRequest request) {
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }

    /**
     * 通过标签查询用户 （暂时不先使用这种方式）
     *
     * @param tagNameList 用户需要有的标签
     * @return
     */
    @Deprecated
    @Override
    public List<User> searchUserByTagsBySQL(List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            // 抛出一个自定义的异常类
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        for (String tagName : tagNameList) {
            queryWrapper = queryWrapper.like("tags", tagName);
        }
        List<User> userList = userMapper.selectList(queryWrapper);
        return userList.stream().map(this::getSafetyUser).collect(Collectors.toList());
    }

    /**
     * 使用内存方式查询 user
     *
     * @param tagNameList 需要有的 user
     * @return
     */
    @Override
    public List<User> searchUserByTagsByMemory(List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            // 抛出一个自定义的异常类
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        List<User> userList = userMapper.selectList(queryWrapper);
        Gson gson = new Gson();
        // 这里可以使用 stream api
        return userList.stream().filter(user -> {
            String tagString = user.getTags();
            Set<String> tagNameSet = gson.fromJson(tagString, new TypeToken<Set<String>>() {
            }.getType());
            // 可以使用 Java8 新特性进行减少圈复杂度
            tagNameSet = Optional.ofNullable(tagNameSet).orElse(new HashSet<>());
            for (String tagName : tagNameList) {
                 if (!tagNameSet.contains(tagName)) {
                     return false;
                 }
            }
            return true;
        }).map(this::getSafetyUser).collect(Collectors.toList());

    }

    /**
     * 更新用户信息
     *
     * @param user 需要修改的用户信息
     * @param loginUser 当前登录的用用户
     * @return
     */
    @Override
    public int updateUser(User user, User loginUser) {
        long userId = loginUser.getId();
        if (userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 进行鉴权，看看是否有权限修改用户的信息
        if (!isAdmin(loginUser) && userId != user.getId()) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        User oldUser = userMapper.selectById(userId);
        if (oldUser == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        // 更新用户并返回
        return userMapper.updateById(user);
    }

    /**
     *获取当前登录的信息
     *
     * @param request
     * @return
     */
    @Override
    public User getLoginUser(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if (userObj == null) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        return (User) userObj;
    }

    /**
     * 进行鉴权，仅管理员可以查询
     *
     * @param request request 请求
     * @return true-管理员   false-代表普通用户
     */
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        // 进行鉴权，仅管理员可以查询
        User user = (User) request.getSession().getAttribute(USER_LOGIN_STATE);
        return user != null && user.getUserRole() == ADMIN_ROLE;
    }

    /**
     * 对健全方法进行重载
     *
     * @param loginUser
     * @return
     */
    @Override
    public boolean isAdmin(User loginUser) {
        // 进行鉴权，仅管理员可以查询
        return loginUser != null && loginUser.getUserRole() == ADMIN_ROLE;
    }
}




