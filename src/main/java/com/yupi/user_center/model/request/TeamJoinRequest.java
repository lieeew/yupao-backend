package com.yupi.user_center.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
/**
 * @author leikooo
 * @create 2023-09-07 21:58
 * @Package com.yupi.user_center.model.request
 * @Description
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TeamJoinRequest implements Serializable {
    private static final long serialVersionUID = -4894468952406021507L;

    /**
     * id 队伍 id
     */
    private Long id;

    /**
     * 密码
     */
    private String password;

}
