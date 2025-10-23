package com.eport.daemon.rule.utils;

import com.eport.common.core.util.SpringContextHolder;
import com.eport.daemon.rule.common.EngineSourceType;
import com.eport.daemon.rule.common.EngineType;
import com.eport.daemon.rule.common.RuleEngineConfigConsent;
import com.eport.daemon.rule.engine.RuleEngine;
import com.eport.daemon.rule.engine.RuleEngineBuilder;
import com.eport.daemon.rule.pojo.RuleConfig;
import com.eport.daemon.rule.storage.ReloadWatcher;

public class RuleEngineHelper {


    public static <T> RuleEngine<T> bulidRuleEngine(RuleConfig config, Class<?> contextClass) {
        return bulidRuleEngine(EngineSourceType.LOCAL, config, contextClass);
    }

    public static <T> RuleEngine<T> bulidRuleEngine(EngineSourceType engineSourceType, RuleConfig config, Class<?> contextClass) {
        return bulidRuleEngine(EngineType.LITEFLOW, engineSourceType, config, null, contextClass);
    }

    public static <T> RuleEngine<T> bulidRuleEngine(EngineType engineType, EngineSourceType engineSourceType, RuleConfig config) {
        return bulidRuleEngine(engineType, engineSourceType, config, null, null);
    }

    public static <T> RuleEngine<T> bulidRuleEngine(EngineType engineType, EngineSourceType engineSourceType, RuleConfig config, ReloadWatcher reloadWatcher, Class<?> contextClass) {
        RuleEngineBuilder<T> ruleEngineBuilder = SpringContextHolder.getBean(RuleEngineBuilder.class);
        return bulidRuleEngine(ruleEngineBuilder, engineType, config, true, engineSourceType, reloadWatcher, contextClass, true);
    }

    public static <T> RuleEngine<T> bulidRuleEngine(RuleEngineBuilder<T> ruleEngineBuilder, EngineType engineType, RuleConfig config, Boolean isInit, EngineSourceType engineSourceType, ReloadWatcher reloadWatcher, Class<?> contextClass, Boolean isStart) {
        RuleEngine<T> engine = ruleEngineBuilder
                .builder()
                .type(engineType)
                .config(config)
                .init(isInit)
                .loadSource(engineSourceType)
                .watch(reloadWatcher)
                .context(contextClass)
                .build();
        if (isStart) {
            engine.start();
        }
        return engine;
    }
}
