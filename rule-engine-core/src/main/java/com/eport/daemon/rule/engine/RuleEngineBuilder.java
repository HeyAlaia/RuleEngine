package com.eport.daemon.rule.engine;


import com.eport.daemon.rule.common.EngineSourceType;
import com.eport.daemon.rule.common.EngineType;
import com.eport.daemon.rule.pojo.RuleConfig;
import com.eport.daemon.rule.storage.ReloadWatcher;
import com.eport.daemon.rule.storage.impl.RedisRuleLoader;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RuleEngineBuilder<T> {

    @Resource
    private RuleEngineFactory<T> ruleEngineFactory;

    private RuleConfig config;
    private EngineType engineType;
    private EngineSourceType engineSourceType;
    private ReloadWatcher reloadWatcher;
    private boolean initialize;
    private Class<?>[] contextClass;

    public RuleEngineBuilder<T> builder() {
        return this;
    }

    public RuleEngineBuilder<T> config(RuleConfig config) {
        this.config = config;
        return this;
    }

    public RuleEngineBuilder<T> type(EngineType engineType) {
        this.engineType = engineType;
        return this;
    }

    public RuleEngineBuilder<T> init(boolean initialize) {
        this.initialize = initialize;
        return this;
    }

    public RuleEngineBuilder<T> loadSource(EngineSourceType engineSourceType) {
        this.engineSourceType = engineSourceType;
        return this;
    }

    public RuleEngineBuilder<T> watch(ReloadWatcher reloadWatcher) {
        this.reloadWatcher = reloadWatcher;
        return this;
    }

    public RuleEngineBuilder<T> context(Class<?>... contextClass) {
        this.contextClass = contextClass;
        return this;
    }

    public RuleEngine<T> build() {
        AbstractRuleEngine<T> ruleEngine = (AbstractRuleEngine<T>) ruleEngineFactory.createRuleEngine(engineType, engineSourceType, contextClass);
        ruleEngine.setConfig(config);
        ruleEngine.setReloadWatcher(reloadWatcher);
        if (initialize) {
            ruleEngine.init();
        }
        return ruleEngine;
    }
}