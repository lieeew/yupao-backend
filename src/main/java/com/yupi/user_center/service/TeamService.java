package com.yupi.user_center.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.user_center.model.domain.Team;
import com.yupi.user_center.model.domain.User;

/**
* @author liang
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2023-09-06 17:24:50
*/
public interface TeamService extends IService<Team> {

    /**
     * 创建队伍表
     *
     * @param team team 对象
     * @param loginUser 当前登录地用户
     * @return
     */
    long addTeam(Team team, User loginUser);
}
