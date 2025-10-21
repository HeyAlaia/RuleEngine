package com.eport.daemon.rule.engine.impl;

import cn.hutool.core.collection.ListUtil;
import com.alibaba.fastjson.JSONObject;
import com.eport.daemon.rule.engine.AbstractRuleEngine;
import com.eport.daemon.rule.pojo.RuleSet;
import com.eport.daemon.rule.storage.RuleStorage;
import com.yomahub.liteflow.builder.el.LiteFlowChainELBuilder;
import com.yomahub.liteflow.core.FlowExecutor;
import com.yomahub.liteflow.core.FlowExecutorHolder;
import com.yomahub.liteflow.flow.LiteflowResponse;
import com.yomahub.liteflow.parser.helper.ParserHelper;
import com.yomahub.liteflow.property.LiteflowConfig;
import com.yomahub.liteflow.property.LiteflowConfigGetter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Alaia
 * @Description: LiteFlowRuleEngine - 基于LiteFlow的规则引擎实现
 **/
@Slf4j
@NoArgsConstructor
public class LiteFlowRuleEngine extends AbstractRuleEngine {

    private final Set<String> CHAIN_NAME_SET = new HashSet<>();
    private FlowExecutor flowExecutor;
    private LiteflowConfig liteflowConfig;

    @Override
    public void open(Map<String, Object> config) {
        RuleStorage storage = getRuleStorage();
        if (flowExecutor != null) {
            throw new IllegalStateException(
                    "LiteFlowRuleEngine已经初始化, 无法再次初始化"
            );
        }
        log.info("开始进行 LiteFlow Rule Engine 初始化工作.");

        // 初始化LiteFlow配置
        initLiteFlowConfig(config);

        // 初始化FlowExecutor
        flowExecutor = FlowExecutorHolder.loadInstance(liteflowConfig);

        // 创建或更新规则链
        createOrUpdate(storage);

        log.info("LiteFlow Rule Engine 初始化工作完成.");
    }

    private void initLiteFlowConfig(Map<String, Object> config) {
        liteflowConfig = LiteflowConfigGetter.get();
    }

    private void createOrUpdate(RuleStorage storage) {
        Map<String, RuleSet> ruleSets = storage.getRuleSet();
        if (ruleSets != null && !ruleSets.isEmpty()) {
            //更新每个规则集
            for (Map.Entry<String, RuleSet> entry : ruleSets.entrySet()) {
                String key = entry.getKey();
                RuleSet ruleSet = entry.getValue();

                try {
                    // 构建规则链定义
                    String chainDefinition = checkChain(ruleSet);
                    dynamicBuildChain(chainDefinition);
                    log.info("完成规则集加载: {}", key);
                } catch (Exception e) {
                    log.error(
                            "加载规则集失败: {}, 错误: {}",
                            key,
                            e.getMessage(),
                            e
                    );
                }
            }
        }
    }

    private void dynamicBuildChain(String chainDefinition) throws DocumentException {
        List<Document> documentList = ListUtil.toList(new Document[0]);
        Document document = DocumentHelper.parseText(chainDefinition);
        documentList.add(document);
        ParserHelper.parseChainDocument(documentList, this.CHAIN_NAME_SET, ParserHelper::parseOneChainEl);
    }

    private String checkChain(RuleSet ruleSet) {
        String ruleContent = ruleSet.getRuleContent();
        if (
                StringUtils.isNotBlank(ruleContent) &&
                        (ruleContent.trim().startsWith("<?xml") ||
                                ruleContent.trim().startsWith("<flow>"))
        ) {
            return ruleContent;
        } else {
            throw new IllegalArgumentException("liteflow规则文件不合法");
        }
    }

    @Override
    public void stop() {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            log.error("停止引擎有误, 当前错误为: {}", e.getMessage());
        }

        if (flowExecutor != null) {
            try {
                flowExecutor = null;
                log.info("LiteFlow引擎已停止");
            } catch (Exception e) {
                log.error("停止LiteFlow引擎时发生错误", e);
            }
        }
    }

    @Override
    public void refresh(RuleStorage storage) {
        log.info(
                "==================== 更新 LiteFlow Rule Chains ===================="
        );

        try {
            createOrUpdate(storage);
        } catch (Exception e) {
            log.error("刷新LiteFlow规则时发生错误", e);
        }

        log.info(
                "==================== 更新 LiteFlow Rule Chains 完成 ===================="
        );
    }

    @Override
    public void exec(JSONObject obj) {
        // 使用决策路由表的方式执行
        flowExecutor.executeRouteChain(obj, JSONObject.class);
    }

    @Override
    public void execute(String chainName, JSONObject obj) {
        if (StringUtils.isNotBlank(chainName)) {
            executeChain(chainName, obj);
        }
    }

    private void executeChain(String chainName, JSONObject obj) {
        if (flowExecutor == null) {
            log.error("FlowExecutor未初始化");
            return;
        }

        LiteflowResponse liteflowResponse = simulateRuleExecution(chainName, obj);
        if (!liteflowResponse.isSuccess()){
            Exception e = liteflowResponse.getCause();
            log.error(
                    "执行规则链 {} 时发生错误: {}",
                    chainName,
                    e.getMessage(),
                    e
            );
        }
    }

    private LiteflowResponse simulateRuleExecution(String chainName, JSONObject obj) {
         return flowExecutor.execute2Resp(chainName, obj, JSONObject.class);
    }
}
