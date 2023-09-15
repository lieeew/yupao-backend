package com.yupi.user_center.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.user_center.model.domain.Team;
import com.yupi.user_center.model.domain.User;
import com.yupi.user_center.model.dto.TeamQuery;
import com.yupi.user_center.model.request.TeamJoinRequest;
import com.yupi.user_center.model.request.TeamQuitTeam;
import com.yupi.user_center.model.request.TeamRemoveRequest;
import com.yupi.user_center.model.request.TeamUpdateRequest;
import com.yupi.user_center.model.vo.TeamUserVO;

import java.util.List;

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

    /**
     * 查询队伍
     *
     * @param teamQuery
     * @return
     */
    List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin);

    /**
     * 更新队伍信息
     *
     * @param teamUpdateRequest
     * @return
     */
    boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser);

    /**
     * 添加队伍
     *
     * @param teamJoinRequest
     * @param loginUser
     * @return
     */
    boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser);

    /**
     * 用户退出队伍
     *
     * @param teamQuitTeam
     * @param loginUser
     * @return
     */
    boolean quitTeam(TeamQuitTeam teamQuitTeam, User loginUser);

    /**
     * 队长删除队伍
     *
     * @param teamRemoveRequest
     * @param loginUser
     * @return
     */
    boolean removeTeam(TeamRemoveRequest teamRemoveRequest, User loginUser);

    /**
     * 获取队伍
     *
     * @param teamId 队伍 id
     * @return 队伍对象
     */
    Team getTeamById(Long teamId);
}
