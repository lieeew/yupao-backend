package com.yupi.user_center.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.user_center.mapper.TeamMapper;
import com.yupi.user_center.model.domain.Team;
import com.yupi.user_center.service.TeamService;
import org.springframework.stereotype.Service;

/**
* @author liang
* @description 针对表【team(队伍)】的数据库操作Service实现
* @createDate 2023-09-06 17:24:50
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService {

}




