package com.yupi.user_center.service;

import com.yupi.user_center.mapper.ListTeamUserMapper;
import com.yupi.user_center.model.domain.User;
import com.yupi.user_center.model.vo.TeamUserVO;
import com.yupi.user_center.model.vo.UserVO;
import org.apache.ibatis.jdbc.SqlRunner;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author leikooo
 * @create 2023-09-10 15:48
 * @Package com.yupi.user_center.service
 * @Description
 */
@SpringBootTest
public class MyBatisTest {
    @Resource
    private SqlRunner sqlRunner;

    @Resource
    private ListTeamUserMapper listTeamUserMapper;

    @Test
    public void testOriginSQL() throws SQLException {
//        System.out.println("sqlRunner = " + sqlRunner);
        Map<String, Object> creatUser = sqlRunner.selectOne("select *\n" +
                "from `team` t\n" +
                "         left join `user_team` ut on t.userId = ut.userId\n" +
                "         left join `user` u on u.id = t.id\n" +
                "where u.id = ?", 1);
        UserVO userVO = new UserVO();
        userVO.setAvatarUrl((String) creatUser.getOrDefault("AVATARURL", "https://zos.alipayobjects.com/rmsportal/jkjgkEfvpUPVyRjUImniVslZfWPnJuuZ.png"));
        userVO.setId((Long) creatUser.get("USERID"));
        userVO.setUserAccount((String) creatUser.get("USERACCOUNT"));
        userVO.setGender((Integer) creatUser.get("GENDER"));
        userVO.setEmail((String) creatUser.get("EMAIL"));
        userVO.setPhone((String) creatUser.get("PHONE"));
        userVO.setPlanetCode((String) creatUser.get("PLANETCODE"));
        userVO.setTags((String) creatUser.get("TAGS"));
    }

    @Test
    public void test2() {
        List<UserVO> creatUser = listTeamUserMapper.listTeamUsers(1L);
        System.out.println("creatUser = " + creatUser);
    }
}
