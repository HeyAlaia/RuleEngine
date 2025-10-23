package com.eport.daemon.rule.storage.impl;

import cn.hutool.core.codec.Base64;
import com.alibaba.fastjson.JSONObject;
import com.eport.daemon.rule.common.EngineSourceType;
import com.eport.daemon.rule.common.RuleEngineConfigConsent;
import com.eport.daemon.rule.pojo.RuleSet;
import com.eport.daemon.rule.storage.RuleLoader;
import com.eport.daemon.rule.utils.RedisHelper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
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
            String content = redisHelper.getStringByKey(setKey);
            RuleSet ruleSet = new RuleSet();
            String topic = setKey.substring(setKey.lastIndexOf(".") + 1);
            ruleSet.setTopic(EngineSourceType.fromString(EngineSourceType.REDIS));
            ruleSet.setRuleSetKey(EngineSourceType.REDIS + topic);
            RuleSet rule = JSONObject.parseObject(content, RuleSet.class);
            String ruleContent = rule.getRuleContent();
            if (rule.getBase64()) {
                ruleContent = Base64.decodeStr(ruleContent);
            }
            ruleSet.setBase64(false);
            ruleSet.setRuleContent(ruleContent);
            ruleSetMap.put(ruleSet.getRuleSetKey(), ruleSet);
        }
        log.info("==================== 从Redis加载规则结束 ====================");
        return ruleSetMap;
    }
}
