package com.eport.daemon.rule.storage;

import com.eport.daemon.rule.pojo.RuleSet;

import java.util.Map;

/**
 * @Author: Alaia
 * @Description: 规则库
 **/
public interface RuleStorage {


    public void load();

    public boolean reload(long timestamp);

    public void clean();

    public Map<String, RuleSet> getRuleSet();

    void addWatcher(ReloadWatcher reloadWatcher);

    void enableWatch(boolean enable);
}
