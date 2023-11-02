package com.yupi.user_center.utils;

import com.yupi.user_center.common.ErrorCode;
import com.yupi.user_center.exception.BusinessException;
import com.yupi.user_center.model.domain.Team;
import org.apache.commons.beanutils.BeanUtils;


/**
 * @author leikooo
 * @Description
 */
public class CopyUtils {

    /**
     * 返回复制信息后的 team 对象
     *
     * @param obj
     * @return
     */
    public static Team getInformation(Object obj) {
        Team team = new Team();
        try {
            BeanUtils.copyProperties(team, obj);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return team;
    }
}
