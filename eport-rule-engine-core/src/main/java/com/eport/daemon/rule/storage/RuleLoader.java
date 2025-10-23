package com.eport.daemon.rule.storage;

import com.eport.daemon.rule.pojo.RuleSet;

import java.util.Map;

/**
 * @Author: Alaia
 * @Description: 规则加载器
 **/
@FunctionalInterface
public interface RuleLoader {

    Map<String, RuleSet> load();

}
