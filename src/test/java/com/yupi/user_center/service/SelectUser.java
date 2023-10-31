package com.yupi.user_center.service;

import com.yupi.user_center.mapper.ListTeamUserMapper;
import com.yupi.user_center.model.vo.UserVO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import javax.annotation.Resource;
import java.util.List;

/**
 * @author leikooo
 * @create 2023-09-14 12:16
 * @Package com.yupi.user_center.service
 * @Description
 */
@SpringBootTest
public class SelectUser {
    @Resource
    private ListTeamUserMapper listTeamUserMapper;

    @Test
    public void test() {
        List<UserVO> teamUsers = listTeamUserMapper.listTeamUsers(1L);
        System.out.println(teamUsers);
    }

}
