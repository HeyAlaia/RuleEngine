package com.eport.daemon.rule.storage.impl;

import com.alibaba.fastjson.JSONObject;
import com.eport.daemon.rule.common.EngineSourceType;
import com.eport.daemon.rule.common.RuleEngineConfigConsent;
import com.eport.daemon.rule.pojo.Rule;
import com.eport.daemon.rule.pojo.RuleSet;
import com.eport.daemon.rule.storage.RuleLoader;
import com.eport.daemon.rule.utils.RedisHelper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: Alaia
 * @Description:
 **/
@Slf4j
@Component
public class RedisRuleLoader implements RuleLoader {

    @Resource
    private RedisHelper redisHelper;

    private final String ruleKeyPrefix;

    public RedisRuleLoader(Map<String, Object> config) {
        this.ruleKeyPrefix = (String) config.getOrDefault(RuleEngineConfigConsent.STORAGE_REDIS_RULES_PREFIX, "rules.engine.set.*");
    }

    @Override
    public Map<String, RuleSet> load() {
        log.info("==================== 开始从Redis加载规则 ====================");
        //获取所有规则引擎的规则
        Set<String> keys = redisHelper.keys(ruleKeyPrefix);
        //定义新的规则存储集合
        Map<String, RuleSet> ruleSetMap = new ConcurrentHashMap<>();
        //处理所有规则
        for (String setKey : keys) {
            Map<Object, Object> ruleSetAll = redisHelper.hgetAll(setKey);
            RuleSet ruleSet = new RuleSet();
            String topic = setKey.substring(setKey.lastIndexOf(".") + 1);
            ruleSet.setTopic(EngineSourceType.REDIS + topic);
            ruleSet.setRuleSetKey(setKey);
            ruleSet.setRuleSetCount(ruleSetAll.size());
            List<Rule> rules = new ArrayList<>();
            ruleSet.setRules(rules);
            for (Map.Entry<Object, Object> entry : ruleSetAll.entrySet()) {
                String ruleJson = entry.getValue().toString();
                Rule rule = JSONObject.parseObject(ruleJson, Rule.class);
                //规则文件经过base64
                if (rule.getBase64()) {
                    String ruleContent = rule.getRuleContent();
                    byte[] decode = Base64.getDecoder().decode(ruleContent.getBytes(StandardCharsets.UTF_8));
                    rule.setRuleContent(new String(decode, StandardCharsets.UTF_8));
                }
                rules.add(rule);
            }
            log.info("加载: {}, 规则数量: {}", ruleSet.getRuleSetKey(), ruleSet.getRuleSetCount());
            ruleSetMap.put(setKey, ruleSet);
        }
        log.info("==================== 从Redis加载规则结束 ====================");
        return ruleSetMap;
    }
}
