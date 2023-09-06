package com.yupi.user_center.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.user_center.mapper.UserTeamMapper;
import com.yupi.user_center.model.domain.UserTeam;
import com.yupi.user_center.service.UserTeamService;
import org.springframework.stereotype.Service;

/**
* @author liang
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
* @createDate 2023-09-06 17:27:06
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService {

}




