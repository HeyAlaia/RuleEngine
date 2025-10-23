package com.eport.daemon.rule.engine;

import com.eport.daemon.rule.pojo.RuleConfig;

import java.util.Map;

/**
 * @Author: Alaia
 *
 * @Description: 规则引擎
 **/
public interface RuleEngine<T> {

    void init(RuleConfig config);
    void init();
    void start();

    void close();

    void execute(T obj);

    void execute(String topic, T obj);

}
