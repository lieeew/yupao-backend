package com.yupi.user_center.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.user_center.common.BaseResponse;
import com.yupi.user_center.common.ErrorCode;
import com.yupi.user_center.common.ResultUtils;
import com.yupi.user_center.exception.BusinessException;
import com.yupi.user_center.model.domain.User;
import com.yupi.user_center.model.request.UserLoginRequest;
import com.yupi.user_center.model.request.UserRegisterRequest;
import com.yupi.user_center.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author leikooo
 * @create 2023-07-03 12:42
 * @Package com.yupi.user_center.controller
 * @Description 用户接口，用户登录系统
 */
@RestController
@RequestMapping("/user")
// 后端写跨域比较合理
@CrossOrigin(origins = {"http://127.0.0.1:5173/", "http://localhost:3000/"}, allowCredentials = "true", allowedHeaders = "*")
@Slf4j
public class UserController {
    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 用户注册
     *
     * @param userRegisterRequest 建议封装一个对象
     * @return userId
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
//            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String planetCode = userRegisterRequest.getPlanetCode();
        // 稍微简单校验一下
        // controller 层一般是没有感情的校验，不涉及逻辑校验
        if (StringUtils.isAllBlank(userAccount, userPassword, checkPassword, planetCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        long result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        return ResultUtils.success(result);
    }

    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request, HttpServletResponse response) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        // 稍微简单校验一下
        // controller 层一般是没有感情的校验，不涉及逻辑校验
        if (StringUtils.isAllBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户或密码为空");
        }

        User result = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(result);
    }

    @PostMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        int result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    @GetMapping("/search")
    public BaseResponse<List<User>> searchUser(String username, HttpServletRequest request) {
        if (!userService.isAdmin(request)) {
            // 返回一个空的集合
//            return ResultUtils.success(new ArrayList<>());
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNoneBlank(username)) {
            queryWrapper.like("username", username);
        }
        List<User> list = userService.list();
        // 这里逻辑要是很复杂的话，那么直接写在 service 里面
        List<User> result = list.stream().map(user -> userService.getSafetyUser(user)
        ).collect(Collectors.toList());
        return ResultUtils.success(result);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(long id, HttpServletRequest request) {
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "id 不存在");
        }
        boolean result = userService.removeById(id);
        return ResultUtils.success(result);
    }

    /**
     * 获取用户当前的状态
     */
    @GetMapping("/current")
    public BaseResponse<User> getUserState(HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        User result = userService.getSafetyUser(user);
        return ResultUtils.success(result);
    }

    @GetMapping("/search/tags")
    public BaseResponse<List<User>> searchUserByTags(@RequestParam(required = false) List<String> tagNameList) {
        // 一般还是校验一下是个否为空，不然可能会欠妥当
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<User> userList = userService.searchUserByTagsByMemory(tagNameList);
        return ResultUtils.success(userList);
    }

    /**
     * 获取用户当前的状态
     */
    @GetMapping("/recommend")
    public BaseResponse<IPage<User>> recommendUsers(HttpServletRequest request, long pageSize, long pageNum) {
        User loginUser = userService.getLoginUser(request);
        // 这里需要设计一个 redis 的 key 这个 key 需要指定的详细一点，因为很多项目可能共用一个 redis
        String redisKey = String.format("yupao:user:recommend:%s", loginUser.getId());
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        IPage<User> userIPage = (IPage<User>) valueOperations.get(redisKey);
        if (userIPage != null) {
            return ResultUtils.success(userIPage);
        }
        // 如果没有缓存那么直接查询数据库
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        // current – 当前页   pageSize – 每页显示条数
        userIPage = userService.page(new Page<>(pageNum, pageSize), queryWrapper);
        try {
            // 放入到 redis 中
            // 这里就算是放入失败也不需要报异常信息
            // redis 一定要设置过期时间
            valueOperations.set(redisKey, userIPage, 10, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("redis set key error " + e);
        }
        return ResultUtils.success(userIPage);
    }

    @PostMapping("/update")
    public BaseResponse<Integer> updateUser(@RequestBody User user, HttpServletRequest request) {
        // 校验参数是否位 null
        if (user == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 校验是否有权限修改
        User loginUser = userService.getLoginUser(request);

        int updated = userService.updateUser(user, loginUser);
        return ResultUtils.success(updated);
    }
}
