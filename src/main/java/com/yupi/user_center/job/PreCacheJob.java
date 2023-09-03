package com.yupi.user_center.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.user_center.common.ResultUtils;
import com.yupi.user_center.model.domain.User;
import com.yupi.user_center.service.UserService;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author leikooo
 * @create 2023-09-03 11:31
 * @Package com.yupi.user_center.job
 * @Description
 */
@Slf4j
public class PreCacheJob {
    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    private List<Long> vipUser = Arrays.asList(1L, 2L, 3L);
    @Scheduled(cron = "0 */1 * * * ?")
    public void doRecommendUser() {
        log.info("定时任务开始执行");
        for (Long id : vipUser) {
            // 这里需要设计一个 redis 的 key 这个 key 需要指定的详细一点，因为很多项目可能共用一个 redis
            String redisKey = String.format("yupao:user:recommend:%s", id);
            ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
            IPage<User> userIPage = (IPage<User>) valueOperations.get(redisKey);
            // 如果没有缓存那么直接查询数据库
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            // current – 当前页   pageSize – 每页显示条数
            userIPage = userService.page(new Page<>(1, 20), queryWrapper);
            try {
                // 放入到 redis 中
                // 这里就算是放入失败也不需要报异常信息
                // redis 一定要设置过期时间
                valueOperations.set(redisKey, userIPage, 1, TimeUnit.MINUTES);
            } catch (Exception e) {
                log.error("redis set key error " + e);
            }
        }
    }

}
