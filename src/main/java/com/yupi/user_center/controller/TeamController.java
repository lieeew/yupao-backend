package com.yupi.user_center.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.user_center.common.BaseResponse;
import com.yupi.user_center.common.ErrorCode;
import com.yupi.user_center.common.ResultUtils;
import com.yupi.user_center.exception.BusinessException;
import com.yupi.user_center.model.domain.Team;
import com.yupi.user_center.model.domain.User;
import com.yupi.user_center.model.dto.TeamQuery;
import com.yupi.user_center.model.request.TeamAddRequest;
import com.yupi.user_center.model.request.TeamJoinRequest;
import com.yupi.user_center.model.request.TeamUpdateRequest;
import com.yupi.user_center.model.vo.TeamUserVO;
import com.yupi.user_center.service.TeamService;
import com.yupi.user_center.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author leikooo
 * @create 2023-07-03 12:42
 * @Package com.yupi.user_center.controller
 * @Description 用户接口，用户登录系统
 */
@RestController
@RequestMapping("/team")
// 后端写跨域比较合理
@CrossOrigin(origins = {"http://127.0.0.1:5173/", "http://localhost:3000/"}, allowCredentials = "true", allowedHeaders = "*")
@Slf4j
public class TeamController {
    @Resource
    private TeamService teamService;

    @Resource
    private UserService userService;

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
    public BaseResponse<Boolean> deleteTeam(@RequestBody long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为 null");
        }
        boolean isSuccess = teamService.removeById(id);
        if (!isSuccess) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除队伍出现错误");
        }
        return ResultUtils.success(true);
    }

    @PutMapping("/update")
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
    public BaseResponse<Team> getTeamById(@RequestBody long id) {
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
     * 获取参数列表
     *
     * @param teamQuery 查询的参数 分装的 DTO类
     * @return
     */
    @GetMapping("/list")
    public BaseResponse<List<TeamUserVO>> getTeamList(@RequestBody TeamQuery teamQuery, HttpServletRequest httpServletRequest) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为 null");
        }
        User user = userService.getLoginUser(httpServletRequest);
        List<TeamUserVO> listList = teamService.listTeams(teamQuery, user);
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
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "加入队伍失败");
        }
        return ResultUtils.success(true);
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
