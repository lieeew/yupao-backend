package com.yupi.user_center.service;

import com.yupi.user_center.model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;

/**
 * @author leikooo
 *
 *  引入新的技术是先写测试类，测试 CRUD
 */
@SpringBootTest
public class RedisTest {
    @Resource
    private RedisTemplate redisTemplate;

    @Test
    public void test() {
        ValueOperations valueOperations = redisTemplate.opsForValue();

        // add
        valueOperations.set("dog", "dog");
        valueOperations.set("leikooo", 21);
        User user = new User();
        user.setId(0L);
        user.setUsername("leikooo");
        valueOperations.set("user", user);

        // select
        Object value = valueOperations.get("dog");
        Assertions.assertTrue("dog".equals(value));

        value = valueOperations.get("leikooo");
        Assertions.assertTrue(21 == (Integer) value);

        System.out.println(valueOperations.get("user"));
        redisTemplate.delete("leikooo");
    }
}