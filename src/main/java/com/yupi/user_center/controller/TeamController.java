package com.yupi.user_center.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.user_center.common.BaseResponse;
import com.yupi.user_center.common.ErrorCode;
import com.yupi.user_center.common.ResultUtils;
import com.yupi.user_center.exception.BusinessException;
import com.yupi.user_center.model.domain.Team;
import com.yupi.user_center.model.domain.User;
import com.yupi.user_center.model.domain.UserTeam;
import com.yupi.user_center.model.dto.TeamQuery;
import com.yupi.user_center.model.request.*;
import com.yupi.user_center.model.vo.TeamUserVO;
import com.yupi.user_center.service.TeamService;
import com.yupi.user_center.service.UserService;
import com.yupi.user_center.service.UserTeamService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author leikooo
 * @create 2023-07-03 12:42
 * @Package com.yupi.user_center.controller
 * @Description 用户接口，用户登录系统
 */
@RestController
@RequestMapping("/team")
// 后端写跨域比较合理
@CrossOrigin(origins = {"http://localhost:3000/"}, allowCredentials = "true", allowedHeaders = "*")
@Slf4j
public class TeamController {
    @Resource
    private TeamService teamService;
    @Resource
    private UserService userService;
    @Resource
    private UserTeamService userTeamService;

    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest httpServletRequest) {
        if (teamAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为 null");
        }
        User loginUser = userService.getLoginUser(httpServletRequest);
        Team team = new Team();
        try {
            BeanUtils.copyProperties(team, teamAddRequest);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        long teamId = teamService.addTeam(team, loginUser);
        return ResultUtils.success(teamId);
    }

    @DeleteMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody TeamRemoveRequest teamRemoveRequest, HttpServletRequest request) {
        if (teamRemoveRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为 null");
        }
        User loginUser = userService.getLoginUser(request);
        boolean isSuccess = teamService.removeTeam(teamRemoveRequest, loginUser);
        if (!isSuccess) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除队伍出现错误");
        }
        return ResultUtils.success(true);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest, HttpServletRequest request) {
        if (teamUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为 null");
        }
        User loginUser = userService.getLoginUser(request);
        boolean updatedById = teamService.updateTeam(teamUpdateRequest, loginUser);
        if (!updatedById) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新队伍出现错误");
        }
        return ResultUtils.success(true);
    }

    @GetMapping("/get")
    public BaseResponse<Team> getTeamById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = teamService.getById(id);
        if (team == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return ResultUtils.success(team);
    }

    /**
     * 获取「队伍」列表
     *
     * @param teamQuery 查询的参数 分装的 DTO类
     * @return
     */
    @PostMapping("/list")
    public BaseResponse<List<TeamUserVO>> getTeamList(@RequestBody TeamQuery teamQuery, HttpServletRequest httpServletRequest) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为 null");
        }
        boolean isAdmin = userService.isAdmin(httpServletRequest);
        List<TeamUserVO> listList = teamService.listTeams(teamQuery, isAdmin);
        return ResultUtils.success(listList);
    }
    /**
     * 获取「我创建」的队伍列表
     *
     * @param teamQuery 查询的参数 分装的 DTO类
     * @return
     */
    @PostMapping("/list/my/create")
    public BaseResponse<List<TeamUserVO>> getMyCreatTeamList(@RequestBody TeamQuery teamQuery, HttpServletRequest httpServletRequest) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为 null");
        }
        User user = userService.getLoginUser(httpServletRequest);
        teamQuery.setUserId(user.getId());
        List<TeamUserVO> listList = teamService.listTeams(teamQuery, true);
        return ResultUtils.success(listList);
    }

    /**
     * 获取「我加入」的队伍列表
     *
     * @param teamQuery 查询的参数 分装的 DTO类
     * @return
     */
    @PostMapping("/list/my/join")
    public BaseResponse<List<TeamUserVO>> getMyJoinTeamList(@RequestBody TeamQuery teamQuery, HttpServletRequest request) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为 null");
        }
        User loginUser = userService.getLoginUser(request);
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", loginUser.getId());
        // 通过 userId 查找出来 teamId 就可以了
        List<UserTeam> userTeamList = userTeamService.list(queryWrapper);
        // 这里就会把类似于
        // teamId  userId
        // 1         2
        // 1         3
        // 2         4
        // 变成了
        // 1 => 2, 3
        // 2 -> 4
        Set<Long> idLists = userTeamList.stream()
                .collect(Collectors.groupingBy(UserTeam::getTeamId))
                .keySet();
        teamQuery.setListIds((new ArrayList<>(idLists)));
        List<TeamUserVO> listList = teamService.listTeams(teamQuery, true);
        return ResultUtils.success(listList);
    }

    /**
     * 加入到队伍
     *
     * @param teamJoinRequest
     * @param request
     * @return
     */
    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestBody TeamJoinRequest teamJoinRequest, HttpServletRequest request) {
        if (teamJoinRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.joinTeam(teamJoinRequest, loginUser);
        if (!result) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "加入队伍失败");
        }
        return ResultUtils.success(true);
    }

    /**
     * 退出队伍
     *
     * @param teamQuitTeam
     * @param request
     */
    @PostMapping("/quitTeam")
    public BaseResponse<Boolean> quitTeam(@RequestBody TeamQuitTeam teamQuitTeam, HttpServletRequest request) {
        if (teamQuitTeam == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean result = teamService.quitTeam(teamQuitTeam, loginUser);
        return ResultUtils.success(result);
    }


    /**
     * 获取参数列表
     *
     * @param teamQuery 查询的参数 分装的 DTO类
     * @return
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<Team>> getTeamListByPage(@RequestBody TeamQuery teamQuery) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为 null");
        }
        Team team = new Team();
        try {
            BeanUtils.copyProperties(team, teamQuery);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        int current = teamQuery.getPageNum();
        int pageSize = teamQuery.getPageSize();
        Page<Team> teamPage = new Page<>(current, pageSize);
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>(team);
        Page<Team> resultPage = teamService.page(teamPage, queryWrapper);
        return ResultUtils.success(resultPage);
    }
}
