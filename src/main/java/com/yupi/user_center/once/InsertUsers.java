package com.yupi.user_center.once;

import com.yupi.user_center.mapper.UserMapper;
import com.yupi.user_center.model.domain.User;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author leikooo
 * @create 2023-08-29 18:43
 * @Package com.yupi.user_center.once
 * @Description
 */
@Component
public class InsertUsers {
    @Resource
    private UserMapper userMapper;

    /**
     * 批量插入用户
     */
//    @Scheduled(initialDelay = 5000, fixedRate = Long.MAX_VALUE)
    public void doInsert() {
        final int INSERT_NUM = 1000000;
        StopWatch stopWatch = new StopWatch();
        System.out.println("good");
        stopWatch.start();
        for (int i = 0; i < INSERT_NUM; i++) {
            User user = new User();
            user.setUsername("fakeLieeew");
            user.setUserAccount("fakeLieeew");
            user.setAvatarUrl("https://zos.alipayobjects.com/rmsportal/jkjgkEfvpUPVyRjUImniVslZfWPnJuuZ.png");
            user.setGender(0);
            user.setUserPassword("");
            user.setPhone("123");
            user.setEmail("123");
            user.setUserStatus(0);
            user.setCreateTime(new Date());
            user.setUpdateTime(new Date());
            user.setIsDelete(0);
            user.setUserRole(0);
            user.setTags("");
            user.setProfile("");
            userMapper.insert(user);
        }
        System.out.println(stopWatch.getTotalTimeMillis());
    }

}
