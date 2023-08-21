package com.yupi.user_center.service;

import com.yupi.user_center.model.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;
import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author leikooo
 * @create 2023-07-02 16:13
 * @Package com.yupi.user_center.service
 * @Description
 */
@SpringBootTest
@Slf4j
class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    public void testAddUser() {
        User user = new User();

        user.setUsername("yupi");
        user.setUserAccount("123");
        user.setAvatarUrl("https://pic.code-nav.cn/user_avatar/1608460212774109186/4LsPUAdz-photo_2023-04-09_16-26-40.jpg");
        user.setGender(0);
        user.setUserPassword("xxx");
        user.setPhone("123");
        user.setEmail("456");
        user.setUserStatus(0);

        boolean result = userService.save(user);
        System.out.println(user.getId());
        // 断言测试
        assertTrue(result);
    }

    @Test
    public void testDigest() {
        String s = DigestUtils.md5DigestAsHex(("123" + "salt").getBytes());
        System.out.println("s = " + s);
    }

    @Test
    void userRegister() {
        String useAccount = "yupi";
        String userPassword = "";
        String checkPassword = "12345678";
        String planetCode = "12";
        long result = userService.userRegister(useAccount, userPassword, checkPassword, planetCode);
        Assert.assertEquals(-1, result);

        // 账户长度
        userPassword = "12345678";
        useAccount = "yu";
        result = userService.userRegister(useAccount, userPassword, checkPassword, planetCode);
        Assert.assertEquals(-1, result);

        // 密码不小于 8 位
        userPassword = "123456";
        checkPassword = "123456";
        result = userService.userRegister(useAccount, userPassword, checkPassword, planetCode);
        Assert.assertEquals(-1, result);

        // 账户不包含特殊字符
        useAccount = "YUPI&$$";
        userPassword = "1234567890";
        checkPassword = "1234567890";
        result = userService.userRegister(useAccount, userPassword, checkPassword, planetCode);
        Assert.assertEquals(-1, result);

        // 密码和校验密码相同
        checkPassword = "123456789";
        userPassword = "1234567890";
        result = userService.userRegister(useAccount, userPassword, checkPassword, planetCode);
        Assert.assertEquals(-1, result);

        // 账户不能重复
        checkPassword = "1234567890";
        useAccount = "yupi";
        userPassword = "1234567890";
        result = userService.userRegister(useAccount, userPassword, checkPassword, planetCode);
        Assert.assertEquals(-1, result);

        // 测试插入成功
        useAccount = "aaaaaaaa";
        result = userService.userRegister(useAccount, userPassword, checkPassword, planetCode);

        Assert.assertTrue(result > 0);
    }

    @Test
    void searchUserByTags() {
//        long startTime = System.currentTimeMillis();
//        List<String> list = Arrays.asList("Java", "Python");
//        List<User> userList = userService.searchUserByTagsBySQL(list);
//        long endTime = System.currentTimeMillis();
//        log.info("usedTime = " + (endTime - startTime));
//        // 断言 275
//        Assert.assertNotNull(userList);
    }
    @Test
    void searchUserByTagsMemory() {
//        long startTime = System.currentTimeMillis();
        List<String> list = Arrays.asList("Java", "Python");
        List<User> userList = userService.searchUserByTagsByMemory(list);
        long endTime = System.currentTimeMillis();
//        log.info("usedTime = " + (endTime - startTime));
        // 断言 295
        Assert.assertNotNull(userList);
    }

}