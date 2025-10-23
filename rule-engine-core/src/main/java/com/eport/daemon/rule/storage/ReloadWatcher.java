package com.eport.daemon.rule.storage;

import com.eport.daemon.rule.engine.AbstractRuleEngine;
import lombok.Setter;

import java.util.Map;

@Setter
public abstract class ReloadWatcher extends Thread {
    private RuleStorage storage;
    private Map<String, Object> config;
    protected AbstractRuleEngine engine;

    public ReloadWatcher(RuleStorage storage, Map<String, Object> config, AbstractRuleEngine engine) {
        this();
        this.storage = storage;
        this.config = config;
        this.engine = engine;
    }

    public ReloadWatcher() {
        this.setDaemon(true);
    }


    @Override
    public void run() {
        /**
         * 这里根据不同的watcher, 实现方式不同
         * zk的是通过添加监听器来实现, 也可以通过实现如轮询的方式来实现
         */
        watch(this.storage, config, engine);
    }

    public abstract void watch(RuleStorage storage, Map<String, Object> config, AbstractRuleEngine engine);

}