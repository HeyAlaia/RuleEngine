package com.eport.daemon.rule.engine;

import com.eport.daemon.rule.common.EngineSourceType;
import com.eport.daemon.rule.common.EngineType;
import com.eport.daemon.rule.engine.impl.DroolsRuleEngine;
import com.eport.daemon.rule.engine.impl.LiteFlowRuleEngine;
import com.eport.daemon.rule.storage.RuleLoader;
import com.eport.daemon.rule.storage.impl.LocalRuleLoader;
import com.eport.daemon.rule.storage.impl.RedisRuleLoader;
import jakarta.annotation.Resource;

import java.util.Objects;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * @Author: Alaia
 * @Description: 规则引擎工厂类
 **/
@Component
public class RuleEngineFactory<T> {

    @Lazy
    @Resource
    private RedisRuleLoader redisRuleLoader;

    @Lazy
    @Resource
    private LocalRuleLoader localRuleLoader;

    public RuleEngine<T> createRuleEngine(EngineType engineType, EngineSourceType engineSourceType, Class<?> contextClass) {
        if (Objects.requireNonNull(engineType) == EngineType.DROOLS) {
            return new DroolsRuleEngine<T>().setRuleLoader(checkSource(engineSourceType));
        } else if (Objects.requireNonNull(engineType) == EngineType.LITEFLOW) {
            return new LiteFlowRuleEngine<T>(contextClass).setRuleLoader(checkSource(engineSourceType));
        }
        throw new IllegalStateException("未知的规则引擎类型, 无法创建.");
    }

    private RuleLoader checkSource(EngineSourceType engineSourceType) {
        if (Objects.requireNonNull(engineSourceType) == EngineSourceType.REDIS) {
            return redisRuleLoader;
        } else if (Objects.requireNonNull(engineSourceType) == EngineSourceType.LOCAL) {
            return localRuleLoader;
        }
        throw new IllegalStateException("未知的规则引擎加载类型, 无法创建.");
    }
}