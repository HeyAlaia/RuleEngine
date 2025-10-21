import com.alibaba.fastjson.JSONObject;
import com.eport.daemon.rule.EportDaemonRuleEngineApplication;
import com.eport.daemon.rule.common.EngineSourceType;
import com.eport.daemon.rule.common.EngineType;
import com.eport.daemon.rule.common.RuleEngineConfigConsent;
import com.eport.daemon.rule.engine.RuleEngine;
import com.eport.daemon.rule.engine.RuleEngineBuilder;
import com.yomahub.liteflow.enums.ParseModeEnum;
import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

import liteflow.LiteFlowTestComponents;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

@Slf4j
@SpringBootTest(classes = EportDaemonRuleEngineApplication.class)
@TestPropertySource(locations = { "classpath:application.yml" })
@Import({LiteFlowTestComponents.class})
public class SimpleLiteFlowTest {

    @Resource
    private RuleEngineBuilder ruleEngineBuilder;

    @Test
    public void testLiteFlowBasic1() {
        log.info("开始测试LiteFlow规则引擎基础功能");

        try {
            //构建引擎
            RuleEngine engine = ruleEngineBuilder
                    .builder()
                    .type(EngineType.LITEFLOW)
                    .config(getLiteFlowConfig())
                    .init(true)
                    .loadSource(EngineSourceType.LOCAL)
                    .build();

            engine.start();

            //测试数据3 - 指定topic执行
            JSONObject topicData = new JSONObject();
            topicData.put("customerLevel", "VIP");
            topicData.put("orderAmount", 888.88);
            topicData.put("weight", 888.88);
            topicData.put("itemCount", 3);

            System.out.println("输入: " + topicData.toJSONString());

            long startTime = System.currentTimeMillis();
            engine.execute("c", topicData);
            long endTime = System.currentTimeMillis();

            System.out.println("输出: " + topicData.toJSONString());
            System.out.println("执行耗时: " + (endTime - startTime) + " ms");

            engine.close();
            log.info("LiteFlow基础功能测试完成");
        } catch (Exception e) {
            log.error("LiteFlow测试过程中发生错误: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Test
    public void testLiteFlowBasic() {
        log.info("开始测试LiteFlow规则引擎基础功能");

        try {
            //构建引擎
            RuleEngine engine = ruleEngineBuilder
                .builder()
                .type(EngineType.LITEFLOW)
                .config(getLiteFlowConfig())
                .init(true)
                .loadSource(EngineSourceType.LOCAL)
                .build();

            engine.start();

            //测试数据1 - 基础商品处理
            JSONObject basicData = new JSONObject();
            basicData.put("text", "LF001");
            basicData.put("weight", 25);
            basicData.put("hasGood", "1");
            basicData.put("price", 99.99);
            basicData.put("category", "general");

            System.out.println("=== 基础商品处理测试 ===");
            System.out.println("输入: " + basicData.toJSONString());

            long startTime = System.currentTimeMillis();
            engine.execute(basicData);
            long endTime = System.currentTimeMillis();

            System.out.println("输出: " + basicData.toJSONString());
            System.out.println("执行耗时: " + (endTime - startTime) + " ms");

            //测试数据2 - 重货处理
            JSONObject heavyData = new JSONObject();
            heavyData.put("text", "LF002");
            heavyData.put("weight", 150);
            heavyData.put("hasGood", "1");
            heavyData.put("price", 299.99);
            heavyData.put("category", "electronics");

            System.out.println("\n=== 重货电子产品测试 ===");
            System.out.println("输入: " + heavyData.toJSONString());

            startTime = System.currentTimeMillis();
            engine.execute(heavyData);
            endTime = System.currentTimeMillis();

            System.out.println("输出: " + heavyData.toJSONString());
            System.out.println("执行耗时: " + (endTime - startTime) + " ms");

            //测试数据3 - 指定topic执行
            JSONObject topicData = new JSONObject();
            topicData.put("customerLevel", "VIP");
            topicData.put("orderAmount", 888.88);
            topicData.put("itemCount", 3);

            System.out.println("\n=== VIP客户折扣测试 ===");
            System.out.println("输入: " + topicData.toJSONString());

            startTime = System.currentTimeMillis();
            engine.execute("chain1", topicData);
            endTime = System.currentTimeMillis();

            System.out.println("输出: " + topicData.toJSONString());
            System.out.println("执行耗时: " + (endTime - startTime) + " ms");

            engine.close();
            log.info("LiteFlow基础功能测试完成");
        } catch (Exception e) {
            log.error("LiteFlow测试过程中发生错误: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Test
    public void testLiteFlowPerformance() {
        log.info("开始LiteFlow性能测试");

        RuleEngine engine = ruleEngineBuilder
            .builder()
            .type(EngineType.LITEFLOW)
            .config(getLiteFlowConfig())
            .init(true)
            .loadSource(EngineSourceType.LOCAL)
            .build();

        engine.start();

        JSONObject testData = new JSONObject();
        testData.put("weight", 50);
        testData.put("hasGood", "1");
        testData.put("price", 200.0);
        testData.put("category", "test");

        int testCount = 10;
        long totalTime = 0;

        System.out.println("=== 性能测试开始 ===");
        System.out.println("测试次数: " + testCount);

        for (int i = 0; i < testCount; i++) {
            JSONObject cloneData = (JSONObject) testData.clone();
            cloneData.put("testIndex", i);

            long startTime = System.currentTimeMillis();
            engine.execute(cloneData);
            long endTime = System.currentTimeMillis();

            long executionTime = endTime - startTime;
            totalTime += executionTime;

            if (i % 5 == 0) {
                System.out.println(
                    "第" + (i + 1) + "次执行耗时: " + executionTime + " ms"
                );
            }
        }

        double avgTime = (double) totalTime / testCount;
        System.out.println("=== 性能测试结果 ===");
        System.out.println("总耗时: " + totalTime + " ms");
        System.out.println(
            "平均耗时: " + String.format("%.2f", avgTime) + " ms"
        );
        System.out.println(
            "每秒处理能力: " +
                String.format("%.0f", 1000.0 / avgTime) +
                " 次/秒"
        );

        engine.close();
        log.info("LiteFlow性能测试完成");
    }

    @Test
    public void testLiteFlowErrorHandling() {
        log.info("开始LiteFlow错误处理测试");

        RuleEngine engine = ruleEngineBuilder
            .builder()
            .type(EngineType.LITEFLOW)
            .config(getLiteFlowConfig())
            .init(true)
            .loadSource(EngineSourceType.LOCAL)
            .build();

        engine.start();

        // 测试空数据
        JSONObject emptyData = new JSONObject();
        System.out.println("=== 空数据测试 ===");
        System.out.println("输入: " + emptyData.toJSONString());
        engine.execute(emptyData);
        System.out.println("输出: " + emptyData.toJSONString());

        // 测试异常数据
        JSONObject badData = new JSONObject();
        badData.put("weight", -10);
        badData.put("hasGood", "invalid");
        badData.put("price", "not_a_number");

        System.out.println("\n=== 异常数据测试 ===");
        System.out.println("输入: " + badData.toJSONString());
        try {
            engine.execute(badData);
            System.out.println("输出: " + badData.toJSONString());
        } catch (Exception e) {
            System.out.println("处理异常: " + e.getMessage());
        }

        // 测试不存在的topic
        JSONObject normalData = new JSONObject();
        normalData.put("test", "value");

        System.out.println("\n=== 不存在Topic测试 ===");
        System.out.println("输入: " + normalData.toJSONString());
        engine.execute("non_existent_topic", normalData);
        System.out.println("输出: " + normalData.toJSONString());

        engine.close();
        log.info("LiteFlow错误处理测试完成");
    }

    private static Map<String, Object> getLiteFlowConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put(RuleEngineConfigConsent.STORAGE_REDIS_RULES_PREFIX, "rules.engine.set.*");
        return config;
    }
}
