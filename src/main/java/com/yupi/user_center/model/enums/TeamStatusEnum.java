package com.yupi.user_center.model.enums;

import io.swagger.models.auth.In;

/**
 * @author leikooo
 * @create 2023-09-07 20:00
 * @Package com.yupi.user_center.model.enums
 * @Description
 */
public enum TeamStatusEnum {
    /**
     * 公开
     */
    PUBLIC(0, "公开"),
    /**
     * 私有
     */
    PRIVATE(1, "私有"),
    /**
     * 加密
     */
    SECRET(2, "加密");


    private int value;

    private String text;

    TeamStatusEnum(int value, String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * 获取对应的 enum
     *
     * @param value
     * @return
     */
    public static TeamStatusEnum getEnumByValue(Integer value) {
        if (value == null) {
            return null;
        }
        TeamStatusEnum[] statusEnums = TeamStatusEnum.values();
        for (TeamStatusEnum statusEnum : statusEnums) {
            if (statusEnum.value == value) {
                return statusEnum;
            }
        }
        return null;
    }
}
