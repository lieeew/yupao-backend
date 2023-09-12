package com.yupi.user_center.service.impl;

import java.util.Date;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import com.yupi.user_center.model.request.TeamUpdateRequest;
import com.yupi.user_center.model.vo.TeamUserVO;
import com.yupi.user_center.model.vo.UserVO;
import com.yupi.user_center.service.TeamService;
import com.yupi.user_center.service.UserService;
import com.yupi.user_center.service.UserTeamService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author liang
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
    public long addTeam(Team team, User loginUser) {
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求的参数为 null");
        }
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        // 登录的 id
        final Long userId = loginUser.getId();

        int maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0);
        if (maxNum < 1 || maxNum > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍的人数不满足要求");
        }
        String title = team.getName();
        if (StringUtils.isBlank(title) || title.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍的标题不满足要求");
        }
        String description = team.getDescription();
        if (StringUtils.isNotBlank(description) && description.length() > 512) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "描述需要小于 512");
        }

        int status = Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
        if (statusEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍状态不满足要求");
        }
        // 如果是 status 是加密状态, 一定要有密码, 且密码 <= 32
        if (TeamStatusEnum.PRIVATE.equals(statusEnum) && (StringUtils.isBlank(team.getPassword()) || team.getPassword().length() > 32)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍密码不满足要求");
        }
        Date expireTime = team.getExpireTime();
        if (new Date().after(expireTime)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "传入的过期时间有误");
        }
        // todo: 有 bug 可能同时创建 100 个用户, 需要加上锁🔒
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        long count = this.count(queryWrapper);
        if (count > 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "只能创建 5 个队伍");
        }
        // 插入队伍信息到队伍表
        Long teamId = team.getId();
        team.setId(null);
        team.setUserId(userId);
        boolean save = this.save(team);
        if (!save || teamId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "保存队伍信息失败");
        }
        // 插入用户 => 队伍关系表
        UserTeam userTeam = new UserTeam();
        userTeam.setTeamId(teamId);
        userTeam.setUserId(userId);
        userTeam.setJoinTime(new Date());

        save = userTeamService.save(userTeam);
        if (!save) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "保存队伍信息失败");
        }
        return teamId;
    }

    /**
     * 查询用队伍
     *
     * @param teamQuery
     * @param loginUser
     * @return
     */
    @Override
    public List<TeamUserVO> listTeams(TeamQuery teamQuery, User loginUser) {
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>();
        Team team = new Team();
        try {
            BeanUtils.copyProperties(team, teamQuery);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        if (teamQuery != null) {
            // 根据队伍 id 进行查询
            Long id = teamQuery.getId();
            if (id != null && id > 0) {
                queryWrapper.eq("id", id);
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
            if (status == null) {
                // 没传入值默认公开
                status = 0;
            }
            if (!userService.isAdmin(loginUser) || !TeamStatusEnum.PUBLIC.equals(TeamStatusEnum.getEnumByValue(status))) {
                throw new BusinessException(ErrorCode.NO_AUTH);
            }
            queryWrapper.eq("status", TeamStatusEnum.getEnumByValue(status));

            // 得到符合要求的条件的队伍, 进行遍历
            List<Team> teamList = this.list(queryWrapper);
            if (CollectionUtils.isEmpty(teamList)) {
                return new ArrayList<>();
            }
            List<TeamUserVO> teamUserVOList = new ArrayList<>();
            for (Team t : teamList) {
                TeamUserVO teamUserVO = new TeamUserVO();
                // 查询到每一个组的 创建者的用户信息
                List<UserVO> creatUser = listTeamUserMapper.listTeamLeader(t.getUserId());
                // 查询到对应组的用户信息
                List<UserVO> teamUsers = listTeamUserMapper.listTeamUsers(t.getId());
                try {
                    BeanUtils.copyProperties(teamUserVO, t);
                } catch (Exception e) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR);
                }
                // 设置相关的信息
                teamUserVO.setCreatUser(creatUser.get(0));
                teamUserVO.setUserList(teamUsers);
                // 添加到集合之中
                teamUserVOList.add(teamUserVO);
            }
            return teamUserVOList;
        }
        return new ArrayList<>();
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
        UserTeam userTeam = userTeamService.getById(id);
        if (userTeam == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
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
        Team team = new Team();
        try {
            BeanUtils.copyProperties(team, teamUpdateRequest);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return this.updateById(team);
    }


    @Override
    public boolean joinTeam(TeamJoinRequest teamJoinRequest, User loginUser) {
        RLock lock = redissonClient.getLock("yupao:teamService:joinTeam:lock");
        try {
            // 只有一个线程能获取到锁
            /*
            第一个参数（等待时间）：表示尝试获取锁的最长等待时间，以毫秒为单位。在这里，传递了 0，表示不等待，如果锁当前不可用，它会立即返回。如果传递了正整数值，它将等待指定的时间来获取锁。

            第二个参数（锁的持有时间）：表示成功获取锁后，锁将被持有的时间，以毫秒为单位。在这里，传递了 -1，表示锁将无限期地被持有，直到显式释放。

            第三个参数（时间单位）：表示等待时间和锁的持有时间的时间单位。在这里，传递了 TimeUnit.MILLISECONDS，表示时间单位是毫秒。你也可以选择其他时间单位，如秒、分钟等，具体取决于你的需求。
             */
            if (lock.tryLock(0, -1, TimeUnit.MILLISECONDS)) {
                Long teamId = teamJoinRequest.getId();
                if (teamId == null || teamId <= 0) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR);
                }
                // 查询到对应的 team 队伍
                Team team = this.getById(teamId);
                if (team == null) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍不存在");
                }
                // 是否过期
                Date expireTime = team.getExpireTime();
                if (expireTime != null && expireTime.before(new Date())) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍已经过期");
                }
                // 私有队伍不能加入
                Integer status = team.getStatus();
                if (TeamStatusEnum.PRIVATE.equals(TeamStatusEnum.getEnumByValue(status))) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "不能加入私密队伍");
                }
                // 加密是否密码是否正确
                String password = team.getPassword();
                if (TeamStatusEnum.SECRET.equals(TeamStatusEnum.getEnumByValue(status))) {
                    if (StringUtils.isBlank(password) || !password.equals(teamJoinRequest.getPassword())) {
                        throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
                    }
                }
                // 「用户」id
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
                UserTeam userTeam = new UserTeam();
                userTeam.setUserId(userId);
                userTeam.setTeamId(teamId);
                userTeam.setJoinTime(new Date());
                userTeam.setIsDelete(0);

                return userTeamService.save(userTeam);
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
}
