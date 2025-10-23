package com.eport.daemon.rule.engine.impl;

import com.alibaba.fastjson.JSONObject;
import com.eport.daemon.rule.engine.AbstractRuleEngine;
import com.eport.daemon.rule.pojo.RuleConfig;
import com.eport.daemon.rule.pojo.RuleSet;
import com.eport.daemon.rule.storage.RuleStorage;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.conf.ClockTypeOption;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Alaia
 * @Description:DroolsRuleEngine
 **/
@Slf4j
@NoArgsConstructor
public class DroolsRuleEngine<T> extends AbstractRuleEngine<T> {

    private final ConcurrentMap<String, KieContainer> kieContainerMap = new ConcurrentHashMap<>();

    @Override
    public void open(RuleConfig config) {
        RuleStorage storage = getRuleStorage();
        if (!kieContainerMap.isEmpty()) {
            throw new IllegalStateException("DroolsRuleEngine已经初始化, 无法再次初始化");
        }
        log.info("开始进行 Drools Rule Engine 初始化工作.");
        createOrUpdate(storage);
        log.info("Drools Rule Engine 初始化工作完成.");
    }

    private void createOrUpdate(RuleStorage storage) {
        Map<String, RuleSet> ruleSets = storage.getRuleSet();
        if (ruleSets != null && !ruleSets.isEmpty()) {
            //更新每个规则集
            for (Map.Entry<String, RuleSet> entry : ruleSets.entrySet()) {
                String key = entry.getKey();
                RuleSet ruleSet = entry.getValue();
                //生成kieContainer
                KieContainer kieContainer = buildKieContainer(ruleSet);
                //更新kieContainer, 这里执行后规则就生效了
                KieContainer oldContainer = kieContainerMap.put(key, kieContainer);
                if (oldContainer != null) {
                    try {
                        TimeUnit.SECONDS.sleep(3);
                    } catch (InterruptedException e) {
                        log.error(e.getMessage(), e);
                    }
                    //这里手动销毁掉
                    oldContainer.dispose();
                }
                log.info("完成: {}", key);
            }
        }
    }


    @Override
    public void stop() {
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            log.error("停止引擎有误, 当前错误为: {}", e.getMessage());
        }
        for (String kieContainerKey : kieContainerMap.keySet()) {
            KieContainer kieContainer = kieContainerMap.get(kieContainerKey);
            kieContainer.dispose();
        }
    }

    @Override
    public void refresh(RuleStorage storage) {
        log.info("==================== 更新 Drools Kie Containers ====================");
        log.info("更新前Containers数量: {}", kieContainerMap.size());
        createOrUpdate(storage);
        log.info("更新后Containers数量: {}", kieContainerMap.size());
        log.info("==================== 更新 Drools Kie Containers 完成 ====================");
    }

    private static KieContainer buildKieContainer(RuleSet ruleSet) {
        KieServices kieServices = KieServices.get();
        KieModuleModel kieModuleModel = kieServices.newKieModuleModel();
        KieBaseModel kieBaseModel = kieModuleModel.newKieBaseModel(ruleSet.getRuleSetKey());
        kieBaseModel.newKieSessionModel(ruleSet.getRuleSetKey())
                .setType(KieSessionModel.KieSessionType.STATEFUL)
                .setClockType(ClockTypeOption.get("pseudo"))
                .setDefault(true);

        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        String fullPath = String.format("src/main/resources/ruleset/" + ruleSet.getTopic() + "/rule_%s.drl", ruleSet.getRuleSetKey());
        kieFileSystem.write(fullPath, ruleSet.getRuleContent());
        kieFileSystem.writeKModuleXML(kieModuleModel.toXML());
        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem).buildAll();
        Results results = kieBuilder.getResults();
        if (results.hasMessages(Message.Level.ERROR)) {
            log.info("rule error:{}", results.getMessages());
            throw new IllegalStateException("rule error");
        }
        return kieServices.newKieContainer(kieServices.getRepository().getDefaultReleaseId());
    }

    @Override
    public void exec(T obj) {
        for (String key : kieContainerMap.keySet()) {
            KieContainer kieContainer = kieContainerMap.get(key);
            KieSession kieSession = kieContainer.newKieSession();
            //事实对象存入kie内存区域
            kieSession.insert(obj);
            //执行所有规则
            kieSession.fireAllRules();
            //销毁kieSession
            kieSession.dispose();
        }
    }

    @Override
    public void execute(String topic, T obj) {
        if(StringUtils.isNotBlank(topic)){
            KieContainer kieContainer = kieContainerMap.get(topic);
            KieSession kieSession = kieContainer.newKieSession();
            //事实对象存入kie内存区域
            kieSession.insert(obj);
            //执行所有规则
            kieSession.fireAllRules();
            //销毁kieSession
            kieSession.dispose();
        }
    }
}
