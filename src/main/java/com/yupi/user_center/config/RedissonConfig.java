package com.yupi.user_center.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.io.IOException;

/**
 * @author leikooo
 * @create 2023-09-05 8:41
 * @Package com.yupi.user_center.config
 * @Description Redisson 配置
 */
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedissonConfig {

    /**
     * 端口
     */
    private String port;

    /**
     * 主机
     */
    private String host;

    private String redissonDatabase;
    @Bean
    public RedissonClient redissonClient() {
        // 1. Create config object
        Config config = new Config();
        // 这里不要写死
        String redissonUrl = String.format("redis://%s:%s", host, port);

        config.useSingleServer().setAddress(redissonUrl).setDatabase(Integer.parseInt(redissonDatabase));

        // 2. Create Redisson instance
        // Sync and Async API
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }

}
