package com.eport.daemon.rule.pojo;

import com.yomahub.liteflow.enums.ParseModeEnum;
import lombok.Data;

@Data
public class RuleConfig {
    private String redisPrefix;
    private String droolsDateformat;
    private ParseModeEnum parseModeEnum = ParseModeEnum.PARSE_ALL_ON_START;
}
