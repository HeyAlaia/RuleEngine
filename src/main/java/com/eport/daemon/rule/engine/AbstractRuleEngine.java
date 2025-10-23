package com.eport.daemon.rule.engine;

import com.alibaba.fastjson.JSONObject;
import com.eport.daemon.rule.common.RuleEngineConfigConsent;
import com.eport.daemon.rule.pojo.RuleConfig;
import com.eport.daemon.rule.storage.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * @Author: Alaia
 **/
@Slf4j
public abstract class AbstractRuleEngine<T> implements RuleEngine<T> {

    private RuleStorage ruleStorage;
    protected RuleLoader ruleLoader;
    protected ReloadWatcher reloadWatcher;
    private volatile boolean running;
    private volatile boolean inited;

    private RuleConfig config;

    protected AbstractRuleEngine() {
    }

    void setConfig(RuleConfig config) {
        this.config = config;
    }

    protected RuleEngine<T> setRuleLoader(RuleLoader ruleLoader) {
        this.ruleLoader = ruleLoader;
        return this;
    }

    protected void setReloadWatcher(ReloadWatcher reloadWatcher) {
        this.reloadWatcher = reloadWatcher;
    }

    protected abstract void open(RuleConfig config);

    protected abstract void stop();

    protected abstract void exec(T obj);

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
    public void init(final RuleConfig config) {
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
        if (Objects.nonNull(this.reloadWatcher)) {
            this.ruleStorage.enableWatch(true);
            this.ruleStorage.addWatcher(this.reloadWatcher);
        }
        //开启规则引擎
        open(config);
        //运行标识修改
        this.running = true;
    }

    private void setBasicEnv(RuleConfig config) {
        Optional.ofNullable(config.getDroolsDateformat()).ifPresent(date -> {
            System.setProperty(RuleEngineConfigConsent.DROOLS_DATE_FORMAT, date);
        });
    }


    @Override
    public void execute(T obj) {
        if (obj != null && this.running) {
            try {
                exec(obj);
            } catch (Exception e) {
                log.error("规则引擎执行错误, 错误数据: {}", obj, e);
            }
        }
    }
}
