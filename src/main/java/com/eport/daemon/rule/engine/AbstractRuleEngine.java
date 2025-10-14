package com.eport.daemon.rule.engine;

import com.alibaba.fastjson.JSONObject;
import com.eport.daemon.rule.common.RuleEngineConfigConsent;
import com.eport.daemon.rule.storage.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @Author: Alaia
 **/
@Slf4j
public abstract class AbstractRuleEngine implements RuleEngine {

    private RuleStorage ruleStorage;
    protected RuleLoader ruleLoader;
    private volatile boolean running;
    private volatile boolean inited;

    private Map<String, Object> config;

    protected AbstractRuleEngine() {
    }

    void setConfig(Map<String, Object> config) {
        this.config = config;
    }

    protected RuleEngine setRuleLoader(RuleLoader ruleLoader) {
        this.ruleLoader = ruleLoader;
        return this;
    }

    protected abstract void open(Map<String, Object> config);

    protected abstract void stop();

    protected abstract void exec(JSONObject obj);

    public abstract void refresh(RuleStorage ruleStorage);

    @Override
    public void close() {
        this.running = false;
        stop();
    }

    protected RuleStorage getRuleStorage() {
        return this.ruleStorage;
    }

    @Override
    public void init(final Map<String, Object> config) {
        if (inited) return;
        if (config == null) throw new NullPointerException("无法初始化规则引擎, 参数为空");
        setBasicEnv(config);
        //创建规则存储
        this.ruleStorage = new GenericRuleStorage(ruleLoader);
        this.inited = true;
    }

    @Override
    public void init() {
        if (config != null) {
            init(config);
        } else {
            throw new NullPointerException("无法初始化规则引擎, 参数为空");
        }
    }

    @Override
    public void start() {
        if (!inited) {
            init();
        }
        //第一次加载
        this.ruleStorage.load();
        //开启监听
        this.ruleStorage.enableWatch(true);
        //开启规则引擎
        open(config);
        //运行标识修改
        this.running = true;
    }

    private void setBasicEnv(Map<String, Object> config) {
        String dateFormat = (String) config.getOrDefault("drools.dateformat", "yyyy-MM-dd HH:mm:ss");
        System.setProperty(RuleEngineConfigConsent.DROOLS_DATE_FORMAT, dateFormat);
    }


    @Override
    public void execute(JSONObject obj) {
        if (obj != null && this.running) {
            try {
                exec(obj);
            } catch (Exception e) {
                log.error("规则引擎执行错误, 错误数据: {}", obj.toJSONString());
            }
            //规则引擎处理成功, 需要标识出来
            obj.put("rule_complete", true);
        }
    }
}
