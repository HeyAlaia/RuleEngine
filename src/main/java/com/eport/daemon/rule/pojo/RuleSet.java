package com.eport.daemon.rule.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: Alaia
 * @ClassName: RuleSet
 * @Description: 规则主题
 * 一个规则集中包含多个规则文件
 * 每个规则文件中有最少一个 rule
 *  * 一个{@link RuleSet} 对应redis中 一个 rule.engine.set.*
 * 一个{@link Rule} 对应redis中rule.engine.set.* 这个map中的一个key,
 * Rule中的ruleContent属性, 对应一个drl文件,一个drl文件中可以有多个drools的rule
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RuleSet {
    private String topic;
    private String ruleSetKey;
    private String ruleContent;
    private Boolean base64;
}
