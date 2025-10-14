package com.eport.daemon.rule.engine;


import com.eport.daemon.rule.common.EngineSourceType;
import com.eport.daemon.rule.common.EngineType;
import com.eport.daemon.rule.storage.impl.RedisRuleLoader;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RuleEngineBuilder {

    @Resource
    private RuleEngineFactory ruleEngineFactory;

    private Map<String, Object> config;
    private EngineType engineType;
    private EngineSourceType engineSourceType;
    private boolean initialize;
    
    public RuleEngineBuilder builder(){
        return this;
    }

    public RuleEngineBuilder config(Map<String, Object> config) {
        this.config = config;
        return this;
    }

    public RuleEngineBuilder type(EngineType engineType) {
        this.engineType = engineType;
        return this;
    }

    public RuleEngineBuilder init(boolean initialize) {
        this.initialize = initialize;
        return this;
    }

    public RuleEngineBuilder loadSource(EngineSourceType engineSourceType) {
        this.engineSourceType = engineSourceType;
        return this;
    }

    public RuleEngine build() {
        AbstractRuleEngine ruleEngine = (AbstractRuleEngine) ruleEngineFactory.createRuleEngine(engineType, engineSourceType);
        ruleEngine.setConfig(config);
        if(initialize){
            ruleEngine.init();
        }
        return ruleEngine;
    }
}