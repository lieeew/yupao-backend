package com.yupi.user_center.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.user_center.UserCenterApplication;
import com.yupi.user_center.model.domain.Team;
import com.yupi.user_center.service.TeamService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import javax.annotation.Resource;

/**
 * @author leikooo
 * @Description
 */
@SpringBootTest(classes = UserCenterApplication.class)
class TeamServiceImplTest {
    @Resource
    private TeamService teamService;

    @Test
    void getSelectedTeam() {
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("name", "Java");
        Page<Team> page = teamService.page(new Page<>(1, 10), queryWrapper);
        System.out.println("page = " + page);
        // teamService.getSelectedTeam(teamQuery, true);
    }
}