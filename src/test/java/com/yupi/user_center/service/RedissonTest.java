package com.yupi.user_center.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.user_center.model.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author leikooo
 * @create 2023-09-05 8:58
 * @Package com.yupi.user_center.service
 * @Description 测试
 */
@SpringBootTest
@Slf4j
public class RedissonTest {

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private UserService userService;

    @Test
    public void test() {
        // list
        List<String> list = new ArrayList<>();
        list.add("leikooo");
        System.out.println("list.get(0) = " + list.get(0));

        RList<Object> rList = redissonClient.getList("test-list");
        rList.add("leikooo");
        System.out.println("rList.get(0) = " + rList.get(0));

        rList.remove(0);

        // map
        RMap<Object, Object> map = redissonClient.getMap("test-map");
        map.put("leikooo", 1);
        map.put("lieeew", 2);

        map.remove("lieeew");
        // set
        RSet<Object> set = redissonClient.getSet("test-set");
        set.add("0");
        set.add("1");
        set.add("2");

        set.remove("0");

        // stack

    }

    @Test
    public void testWatchDog() {
        RLock lock = redissonClient.getLock("yupao:precacheJob:docache:lock");
        try {
            // 这里设置 -1 就会开启自动续期
            if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                log.info("上锁");
                // 如果没有缓存那么直接查询数据库
                QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                // current – 当前页   pageSize – 每页显示条数
                IPage<User> userIPage = userService.page(new Page<>(1, 20), queryWrapper);
                Thread.sleep(100000L);
            }
        } catch (Exception e) {
            log.error("doRecommendUser error " + e);
        } finally {
            // 判断是不是自己上的锁，必需要在这里面写因为 try 里面的代码可能会出现 bug 导致不会解锁
            if (lock.isHeldByCurrentThread()) {
                log.info("释放锁");
                // 解锁
                lock.unlock();
            }
        }
    }

}
