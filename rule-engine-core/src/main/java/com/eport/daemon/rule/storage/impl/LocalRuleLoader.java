package com.eport.daemon.rule.storage.impl;

import cn.hutool.core.util.StrUtil;
import com.eport.daemon.rule.common.EngineSourceType;
import com.eport.daemon.rule.pojo.RuleSet;
import com.eport.daemon.rule.storage.RuleLoader;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @Author: Alaia
 * @Description: 从本地文件系统（默认 classpath 下的规则目录）加载规则的实现。
 */
@Slf4j
@Component
public class LocalRuleLoader implements RuleLoader, ApplicationContextAware { // 实现 ApplicationContextAware 接口

    @Value("${rule.loader.local.path:classpath:rules/}")
    private String ruleRootPath;

    private ApplicationContext applicationContext; // 注入 ApplicationContext

    public LocalRuleLoader() {
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void init() {
        log.info("LocalRuleLoader initialized with ruleRootPath: {}", ruleRootPath);
    }

    @Override
    public Map<String, RuleSet> load() {
        log.info("==================== 开始从 classpath 路径 [{}] 加载规则 ====================", ruleRootPath);
        Map<String, RuleSet> loadedRuleSetMap = new ConcurrentHashMap<>();

        try {
            // 构建 Location Pattern，添加通配符，以便发现目录下的所有文件
            // 例如：classpath:rules/ -> classpath:rules/**/*.{drl,json}
            // 如果 ruleRootPath 已经是文件路径，则不做修改
            String locationPattern;
            if (ruleRootPath.endsWith("/")) {
                locationPattern = ruleRootPath + "**/*.{drl,xml}";
            } else if (!ruleRootPath.contains("*") && !ruleRootPath.contains("?")) { // 如果是单个文件，且不含通配符
                locationPattern = ruleRootPath;
            } else { // 如果 ruleRootPath 已经包含通配符
                locationPattern = ruleRootPath;
            }

            log.info("Using actual location pattern for rule loading: {}", locationPattern);

            // 使用 applicationContext.getResources() 来获取所有匹配的资源
            Resource[] resources = applicationContext.getResources(locationPattern);

            if (resources.length == 0) {
                log.warn("在路径 [{}] 下未找到任何规则文件。", ruleRootPath);
                return loadedRuleSetMap;
            }

            for (Resource res : resources) {
                if (!res.exists() || !res.isReadable()) {
                    log.warn("无法读取资源（不存在或不可读）: {}", res.getDescription());
                    continue;
                }

                try {
                    String fileName = res.getFilename();
                    if (fileName == null || fileName.isEmpty()) {
                        log.warn("无法获取资源文件名，跳过: {}", res.getDescription());
                        continue;
                    }
                    String topic = fileName.substring(0, fileName.lastIndexOf("."));
                    log.debug("Processing rule file: {} with topic: {}", fileName, topic);

                    String ruleContent;
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(res.getInputStream(), StandardCharsets.UTF_8))) {
                        ruleContent = reader.lines().collect(Collectors.joining(System.lineSeparator()));
                    }

                    RuleSet ruleSet = new RuleSet();
                    ruleSet.setTopic(EngineSourceType.fromString(EngineSourceType.LOCAL));
                    ruleSet.setRuleSetKey(EngineSourceType.LOCAL + topic);

                    if (StrUtil.isNotBlank(ruleContent)) {
                        ruleSet.setRuleContent(ruleContent);
                        ruleSet.setBase64(false);
                    } else {
                        log.error("文件内容为空: {}", fileName);
                        continue;
                    }

                    loadedRuleSetMap.put(ruleSet.getRuleSetKey(), ruleSet);
                    log.info("加载: {}, 规则数量: {}, 来源: {}", ruleSet.getRuleSetKey(), 1, res.getDescription());

                } catch (IOException e) {
                    log.error("读取资源失败: {}", res.getDescription(), e);
                } catch (Exception e) {
                    log.error("解析规则文件失败: {}", res.getDescription(), e);
                }
            }

        } catch (IOException e) {
            log.error("无法访问或解析规则资源路径: {}", ruleRootPath, e);
        }

        log.info("==================== 从本地 classpath 加载规则结束，共加载 {} 个规则集 ====================", loadedRuleSetMap.size());
        return loadedRuleSetMap;
    }
}
