package com.yupi.user_center.service;

import com.yupi.user_center.model.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author leikooo
 * @create 2023-08-29 19:35
 * @Package com.yupi.user_center.service
 * @Description
 */
@SpringBootTest
public class InsertUserTest {
    @Resource
    private UserService userService;

    /**
     * 自定义一个线程池
     *
     * 如果事件超过了 ArrayBlockingQueue 的默认长度，
     * 我们就让 corePoolSize 增加到 maximumPoolSize 后面事件再多就会按照默认行为直接 reject
     */
    private ExecutorService executorService = new ThreadPoolExecutor(40, 1000, 10000, TimeUnit.MINUTES, new ArrayBlockingQueue<>(10000));
    /**
     * 批量插入用户
     */
    @Test
    public void doInsert() {
        final int INSERT_NUM = 1000;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Collection<User> users = new ArrayList<>();
        for (int i = 0; i < INSERT_NUM; i++) {
            User user = new User();
            user.setUsername("fakeLeikooo");
            user.setUserAccount("fakeLeikooo");
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
            users.add(user);
        }
        userService.saveBatch(users, 100);
        System.out.println("Insert use time" + stopWatch.getTotalTimeMillis());
    }

    @Test
    public void doConcurrencyInsert() {
        final int INSERT_NUM = 100000;
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        int j = 0;
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Collection<User> users = new ArrayList<>();
            while (true) {
                j++;
                User user = new User();
                user.setUsername("fakeLeikooo");
                user.setUserAccount("fakeLeikooo");
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
                users.add(user);
                if (j % 10000 == 0) {
                    break;
                }
            }
            // 异步执行, 并发。 要注意写在异步的操作，执行顺序没有什么关系的操作
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                System.out.println("ThreadName = " + Thread.currentThread().getName());
                userService.saveBatch(users, 10000);
            }, executorService);
            futures.add(future);
        }
        // 使用 join 之后是执行这个操作之后才会执行下面的操作
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[]{})).join();
        System.out.println("Insert use time" + stopWatch.getTotalTimeMillis());
    }
}
