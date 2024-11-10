package com.yupi.user_center.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.user_center.common.ErrorCode;
import com.yupi.user_center.exception.BusinessException;
import com.yupi.user_center.mapper.ListTeamUserMapper;
import com.yupi.user_center.mapper.TeamMapper;
import com.yupi.user_center.model.domain.Team;
import com.yupi.user_center.model.domain.User;
import com.yupi.user_center.model.domain.UserTeam;
import com.yupi.user_center.model.dto.TeamQuery;
import com.yupi.user_center.model.enums.TeamStatusEnum;
import com.yupi.user_center.model.request.TeamJoinRequest;
import com.yupi.user_center.model.request.TeamQuitTeam;
import com.yupi.user_center.model.request.TeamRemoveRequest;
import com.yupi.user_center.model.request.TeamUpdateRequest;
import com.yupi.user_center.model.vo.TeamUserVO;
import com.yupi.user_center.model.vo.UserVO;
import com.yupi.user_center.service.TeamService;
import com.yupi.user_center.service.UserService;
import com.yupi.user_center.service.UserTeamService;
import com.yupi.user_center.utils.CopyUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author leikooo
 * @description 针对表【team(队伍)】的数据库操作Service实现
 * @createDate 2023-09-06 17:24:50
 */
@Service
@Slf4j
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team> implements TeamService {
    @Resource
    private UserTeamService userTeamService;
    @Resource
    private UserService userService;
    @Resource
    private ListTeamUserMapper listTeamUserMapper;
    @Resource
    private RedissonClient redissonClient;

    /**
     * 创建队伍表
     *
     * @param team      team 对象
     * @param loginUser 当前登录地用户
     * @return
     */
    @Override
    // 实现事务
    @Transactional(rollbackFor = Exception.class)
    public long addTeam(final Team team, final User loginUser) {
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求的参数为 null");
        }
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        final Long userId = loginUser.getId();
        verifyParameters(team);
        RLock lock = redissonClient.getLock("yupao:teamService:addTeam:lock");
        try {
            while (true) {
                if (lock.tryLock(0, -1, TimeUnit.SECONDS)) {
                    return addTeam(userId, team);
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    /**
     * @param userId
     * @param team
     * @return
     */
    private long addTeam(final Long userId, final Team team) {
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        long count = this.count(queryWrapper);
        if (count > 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "只能创建 5 个队伍");
        }
        // 插入队伍信息到队伍表
        Team insertUser = new Team(null, team.getName(), team.getDescription(), Optional.ofNullable(team.getStatus()).orElse(0), team.getExpireTime(), userId, Optional.ofNullable(team.getStatus()).orElse(0), team.getPassword(), team.getExpireTime(), team.getUpdateTime(), 1L, team.getIsDelete());
        boolean save = this.save(insertUser);
        Long teamId = team.getId();
        if (!save || teamId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "保存队伍信息失败");
        }
        // 插入用户 => 队伍关系表
        UserTeam userTeam = new UserTeam(null, userId, teamId, new Date(), new Date(), new Date(), 0);
        save = userTeamService.save(userTeam);
        if (!save) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "保存队伍信息失败");
        }
        return teamId;
    }

    /**
     * 校验对应的 team 参数
     */
    private void verifyParameters(final Team team) {
        final int maxNum = Optional.ofNullable(team.getMaxNum()).orElse(4);
        final String description = team.getDescription();
        final TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(Optional.ofNullable(team.getStatus()).orElse(0));
        final Date expireTime = team.getExpireTime();
        final String password = team.getPassword();
        final String title = team.getName();
        if (maxNum < 1 || maxNum > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍的人数不满足要求");
        }
        if (StringUtils.isNotBlank(description) && description.length() > 512) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "描述需要小于 512");
        }
        if (statusEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍状态不满足要求");
        }
        if (new Date().after(expireTime)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "传入的过期时间有误");
        }
        // 如果是 status 是加密状态, 一定要有密码, 且密码 <= 32
        if (TeamStatusEnum.PRIVATE.equals(statusEnum) && (StringUtils.isBlank(password) || password.length() > 32)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍密码不满足要求");
        }
        if (StringUtils.isBlank(title) || title.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍的标题不满足要求");
        }
    }

    /**
     * 查询用队伍
     *
     * @param teamQuery
     * @param isAdmin
     * @return
     */
    @Override
    public List<TeamUserVO> listTeams(final TeamQuery teamQuery, final boolean isAdmin) {
        Page<Team> teamPageList = getSelectedTeam(teamQuery, isAdmin);
        if (CollectionUtils.isEmpty(teamPageList.getRecords())) {
            return new ArrayList<>();
        }
        return teamPageList.getRecords().stream().map((Team t) -> {
                    // 查询到每一个组的 创建者的用户信息
                    List<UserVO> creatUser = listTeamUserMapper.listTeamLeader(t.getUserId());
                    // 查询到对应组的用户信息
                    List<UserVO> teamUsers = listTeamUserMapper.listTeamUsers(t.getId());
                    return new TeamUserVO(t.getId().toString(), t.getName(), t.getDescription(), t.getMaxNum(), t.getExpireTime(), t.getUserId(), t.getMemberCount(), t.getStatus(), t.getCreateTime(), t.getUpdateTime(), teamUsers, creatUser.get(0));
                }).collect(Collectors.toList());
    }

    private Page<Team> getSelectedTeam(final TeamQuery teamQuery, final boolean isAdmin) {
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        // 根据队伍 id 进行查询
        Long id = teamQuery.getId();
        if (id != null && id > 0) {
            queryWrapper.eq("id", id);
        }
        // 根据 id 集合进行查询，多个队伍
        List<Long> listIds = teamQuery.getListIds();
        if (org.apache.commons.collections.CollectionUtils.isNotEmpty(listIds)) {
            queryWrapper.in("id", listIds);
        }
        // 同时收缩 名称 + 描述
        String searchText = teamQuery.getSearchText();
        if (StringUtils.isNoneBlank(searchText)) {
            // 使用 or 查询写法
            queryWrapper.or(qw -> (qw.like("name", searchText)).or().like("description", searchText));
        }
        // 根据队名称进行查询
        String name = teamQuery.getName();
        if (StringUtils.isNoneBlank(name)) {
            queryWrapper.like("name", name);
        }
        // 只是根据描述进行查询
        String description = teamQuery.getDescription();
        if (StringUtils.isNoneBlank(description)) {
            queryWrapper.like("description", description);
        }
        // 根据最大人数进行查询
        Integer maxNum = teamQuery.getMaxNum();
        if (maxNum != null && maxNum > 0) {
            queryWrapper.eq("maxNum", maxNum);
        }
        // 根据用户 id 进行查询
        Long userId = teamQuery.getUserId();
        if (userId != null && userId > 0) {
            queryWrapper.eq("userId", userId);
        }
        // 不显示已经过期的队伍
        // expireTime is null or expireTime > now()
        queryWrapper.and(qw -> qw.gt("expireTime", new Date()).or().isNull("expireTime"));
        // 根据状态进行查询,  这里需要限制只有 管理员才查看一些非公开的 Team
        Integer status = teamQuery.getStatus();
        if (status != null && status > -1) {
            queryWrapper.eq("status", status);
        }
        if (!isAdmin && !TeamStatusEnum.PUBLIC.equals(TeamStatusEnum.getEnumByValue(status))) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        return this.page(new Page<>(teamQuery.getPageNum(), teamQuery.getPageSize()), queryWrapper);
    }

    /**
     * 更新队伍信息
     *
     * @param teamUpdateRequest
     * @param loginUser
     * @return
     */
    @Override
    public boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser) {
        if (teamUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = teamUpdateRequest.getId();
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team oldTeam = this.getById(id);
        if (!Objects.equals(oldTeam.getUserId(), loginUser.getId()) && !userService.isAdmin(loginUser)) {
            // 既不是管理员也不是创建者
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(teamUpdateRequest.getStatus());
        // 如果要把「公有」对应的房间变成「加密」的房间
        if (TeamStatusEnum.getEnumByValue(oldTeam.getStatus()).equals(TeamStatusEnum.PUBLIC) && statusEnum.equals(TeamStatusEnum.SECRET)) {
            if (StringUtils.isBlank(teamUpdateRequest.getPassword())) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "私密房间需要设置密码");
            }
        }
        Team team = CopyUtils.getInformation(teamUpdateRequest);
        return this.updateById(team);
    }

    @Override
    public boolean joinTeam(final TeamJoinRequest teamJoinRequest, final User loginUser) {
        Long teamId = teamJoinRequest.getId();
        Team team = getTeamById(teamId);
        verifyParams(team, teamJoinRequest.getPassword());
        RLock lock = redissonClient.getLock("yupao:teamService:joinTeam:lock");
        try {
            while (true) {
                if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                    return joinTeam(loginUser, team, teamId);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        return false;
    }

    /**
     * @param loginUser
     * @param team
     * @param teamId
     * @return
     */
    private boolean joinTeam(final User loginUser, final Team team, final Long teamId) {
        Long userId = loginUser.getId();
        // 不能加入自己的队伍
        if (Objects.equals(userId, team.getUserId())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能加入自己的队伍");
        }
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        long count = userTeamService.count(queryWrapper);
        if (count > 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户最多加入 5 个队伍");
        }
        // 不能重复加入已加入的队伍
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        queryWrapper.eq("teamId", teamId);
        count = userTeamService.count(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能重复加入已加入的队伍");
        }
        // 新增队伍 - 用户关联信息
        UserTeam userTeam = new UserTeam(null, userId, teamId, new Date(), new Date(), new Date(), 0);
        // 增加 team 表的 memberCount 人数 + 1
        final Team oldTeam = this.getTeamById(teamId);
        Team changedTeam = oldTeam.changeMemberCount(oldTeam, oldTeam.getMemberCount() + 1L);
        boolean save = this.updateById(changedTeam);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return userTeamService.save(userTeam);
    }

    /**
     * @param team
     * @param password
     */
    private void verifyParams(final Team team, final String password) {
        // 是否过期
        Date expireTime = team.getExpireTime();
        if (expireTime != null && expireTime.before(new Date())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍已经过期");
        }
        // 判断人数是否已满
        if (team.getMemberCount() >= team.getMaxNum()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "人数已满");
        }
        // 私有队伍不能加入
        Integer status = team.getStatus();
        if (TeamStatusEnum.PRIVATE.equals(TeamStatusEnum.getEnumByValue(status))) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能加入私密队伍");
        }
        if (TeamStatusEnum.SECRET.equals(TeamStatusEnum.getEnumByValue(status))) {
            if (StringUtils.isBlank(team.getPassword()) || !team.getPassword().equals(password)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean quitTeam(final TeamQuitTeam teamQuitTeam, final User loginUser) {
        if (teamQuitTeam == null || loginUser == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 队伍 id
        final Long teamId = teamQuitTeam.getId();
        // 获取对应的 team
        final Team team = getTeamById(teamId);
        // 队伍现在的人数
        final Long memberCount = team.getMemberCount();
        final Long userId = loginUser.getId();
        final UserTeam userTeam = new UserTeam(userId, teamId);
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>(userTeam);
        final long count = userTeamService.count(queryWrapper);
        if (count == 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "未加入队伍");
        }
        // 队伍人数 - 1
        Team changedTeam = team.changeMemberCount(team, team.getMemberCount() - 1L);
        boolean result = this.updateById(changedTeam);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        if (memberCount == 1) {
            // 只有一个人，那么就直接删除队伍和队伍的关系
            this.removeById(teamId);
        }
        if (memberCount != 1) {
            // 判断该用户是否为 leader
            if (!Objects.equals(team.getUserId(), userId)) {
                throw new BusinessException(ErrorCode.NO_AUTH, "该用户不是 leader");
            }
            changeLeader(team, teamId);
        }
        // 自己退出队伍
        return userTeamService.remove(queryWrapper);
    }

    /**
     * 修改对应队伍的管理员
     *
     * @param team
     * @param teamId
     */
    private void changeLeader(final Team team, final Long teamId) {
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        userTeamQueryWrapper.eq("teamId", teamId);
        // 只是取出两条数据，可以进行「性能优化」的点
        userTeamQueryWrapper.last("order by id asc limit 2");
        List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);
        if (CollectionUtils.isEmpty(userTeamList) || userTeamList.size() <= 1) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        UserTeam nextUserTeam = userTeamList.get(1);
        Long nextUserTeamId = nextUserTeam.getUserId();
        // 更新当前队伍的队长
        Team savedTeam = new Team(team.getId(), team.getName(), team.getDescription(), team.getMaxNum(), team.getExpireTime(), nextUserTeamId, team.getStatus(), team.getPassword(), team.getCreateTime(), team.getUpdateTime(), team.getMemberCount(), team.getIsDelete());
        boolean result = this.updateById(savedTeam);
        if (!result) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "更新失败");
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeTeam(TeamRemoveRequest teamRemoveRequest, User loginUser) {
        if (teamRemoveRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long teamId = teamRemoveRequest.getId();
        Team team = getTeamById(teamId);
        if (!Objects.equals(team.getUserId(), loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH, "没有权限进行修改");
        }
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamId", teamId);
        boolean remove = userTeamService.remove(queryWrapper);
        if (!remove) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        // 删除队伍
        return this.removeById(teamId);
    }

    /**
     * 校验了是否存在对应的队伍，并且返回对应的队伍对象 （出现两次以上可以考虑封装一下）
     *
     * @param teamId 队伍 id
     * @return
     */
    @Override
    public Team getTeamById(Long teamId) {
        if (teamId == null || teamId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 判断是否存在该队伍
        Team team = this.getById(teamId);
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍不存在");
        }
        return team;
    }
}
