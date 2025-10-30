package com.eport.daemon.rule.config;

import com.eport.daemon.rule.engine.RuleEngineBuilder;
import com.eport.daemon.rule.engine.RuleEngineFactory;
import com.eport.daemon.rule.storage.database.service.RuleDatabaseLoadService;
import com.eport.daemon.rule.storage.database.service.impl.RuleDatabaseLoadServiceImpl;
import com.eport.daemon.rule.storage.impl.DatabaseRuleLoader;
import com.eport.daemon.rule.storage.impl.LocalRuleLoader;
import com.eport.daemon.rule.storage.impl.RedisRuleLoader;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.eport.daemon.rule.storage.database.mapper")
public class RuleEngineAutoConfiguration {

    @Bean
    public RuleEngineBuilder ruleEngineBuilder(){
        return new RuleEngineBuilder();
    }

    @Bean
    public RuleEngineFactory ruleEngineFactory(){
        return new RuleEngineFactory();
    }

    @Bean
    public RuleDatabaseLoadService ruleDatabaseLoadService(){
        return new RuleDatabaseLoadServiceImpl();
    }

    @Bean
    public DatabaseRuleLoader databaseRuleLoader(){
        return new DatabaseRuleLoader();
    }

    @Bean
    public LocalRuleLoader localRuleLoader(){
        return new LocalRuleLoader();
    }

    @Bean
    public RedisRuleLoader redisRuleLoader(){
        return new RedisRuleLoader();
    }
}
