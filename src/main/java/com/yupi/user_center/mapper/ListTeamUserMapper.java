package com.yupi.user_center.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yupi.user_center.model.vo.UserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;

/**
 * @author leikooo
 */
@Mapper
public interface ListTeamUserMapper extends BaseMapper<TeamMapper> {
    /**
     * 查询到对应每一个队伍的 leader
     *
     * @param userId
     * @return
     */
    @Select("select u.* from yupao_backend.team t left join yupao_backend.user u on t.userId = u.id where u.id = #{userId}")
    List<UserVO> listTeamLeader(@Param("userId") Long userId);

    /**
     * 查询到对应每一个队伍的成员
     *
     */
    @Select("select u.* from yupao_backend.team t left join yupao_backend.user_team ut on t.id = ut.teamId left join yupao_backend.user u on u.id = ut.userId where ut.teamId = #{teamId} and u.isDelete = 0 and ut.isDelete = 0 and t.isDelete = 0")
    List<UserVO> listTeamUsers(@Param("teamId") Long teamId);
}