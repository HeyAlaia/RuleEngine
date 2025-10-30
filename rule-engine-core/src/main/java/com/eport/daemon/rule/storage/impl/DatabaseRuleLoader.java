package com.eport.daemon.rule.storage.impl;

import cn.hutool.core.codec.Base64;
import com.eport.daemon.rule.pojo.RuleSet;
import com.eport.daemon.rule.storage.RuleLoader;
import com.eport.daemon.rule.storage.database.service.RuleDatabaseLoadService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: Alaia
 * @Description: 从本地文件系统（默认 classpath 下的规则目录）加载规则的实现。
 */
@Slf4j
@Component
public class DatabaseRuleLoader implements RuleLoader {

    @Resource
    private RuleDatabaseLoadService ruleDatabaseLoadService;


    public DatabaseRuleLoader() {
    }

    @Override
    public Map<String, RuleSet> load() {
        log.info("==================== 开始从 数据库 加载规则 ====================");
        Map<String, RuleSet> loadedRuleSetMap = new ConcurrentHashMap<>();
        ruleDatabaseLoadService.list().forEach(ruleSet -> {
            if (ruleSet.getHasEnable()) {
                RuleSet ruleSet1 = new RuleSet();
                BeanUtils.copyProperties(ruleSet, ruleSet1);
                if (ruleSet1.getBase64()) {
                    String ruleContent = Base64.decodeStr(ruleSet1.getRuleContent());
                    ruleSet1.setRuleContent(ruleContent);
                }
                loadedRuleSetMap.put(ruleSet1.getRuleSetKey(), ruleSet1);
            }
        });
        log.info("==================== 从本地 数据库 加载规则结束，共加载 {} 个规则集 ====================", loadedRuleSetMap.size());
        return loadedRuleSetMap;
    }
}
