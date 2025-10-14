package com.eport.daemon.rule.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @Author: Alaia
 * @Description: 规则
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rule {
    private String ruleName;
    private String ruleId;
    private String ruleContent;
    private String expirationTime;
    private String effectiveTime;
    private Boolean base64;
}
