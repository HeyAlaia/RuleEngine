package com.eport.daemon.rule.common;

import lombok.Getter;

/**
 * @Author: Alaia
 * @Description: 规则引擎类型
 **/
@Getter
public enum EngineSourceType {
    REDIS,
    DATABASE,
    LOCAL;

    public static String fromString(EngineSourceType engineSourceType) {
        return engineSourceType.name().toLowerCase();
    };


}
