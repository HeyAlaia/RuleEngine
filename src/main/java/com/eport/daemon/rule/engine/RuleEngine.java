package com.eport.daemon.rule.engine;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

/**
 * @Author: Alaia
 *
 * @Description: 规则引擎
 **/
public interface RuleEngine {

    void init(Map<String, Object> config);
    void init();
    void start();

    void close();

    void execute(JSONObject obj);

    void execute(String topic, JSONObject obj);

}
