import com.alibaba.fastjson.JSONObject;
import com.eport.daemon.rule.EportDaemonRuleEngineApplication;
import com.eport.daemon.rule.common.EngineSourceType;
import com.eport.daemon.rule.common.EngineType;
import com.eport.daemon.rule.common.RuleEngineConfigConsent;
import com.eport.daemon.rule.engine.RuleEngine;
import com.eport.daemon.rule.engine.RuleEngineBuilder;
import com.eport.daemon.rule.liteflow.LiteFlowDefaultComponents;
import com.eport.daemon.rule.pojo.RuleConfig;
import com.eport.daemon.rule.utils.RuleEngineHelper;
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
@Import({LiteFlowTestComponents.class, LiteFlowDefaultComponents.class})
public class SimpleLiteFlowTest {

    @Test
    public void testLiteFlowBasic1() {
        log.info("开始测试LiteFlow规则引擎基础功能");

        try {
            //构建引擎
            RuleEngine<JSONObject> engine = RuleEngineHelper.bulidRuleEngine(getLiteFlowConfig(), JSONObject.class);

            //测试数据3 - 指定topic执行
            JSONObject topicData = new JSONObject();
            topicData.put("customerLevel", "VIP");
            topicData.put("orderAmount", 888.88);
            topicData.put("weight", 888.88);
            topicData.put("itemCount", 3);

            System.out.println("输入: " + topicData.toJSONString());

            long startTime = System.currentTimeMillis();
            engine.execute("chain1", topicData);
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

    private static RuleConfig getLiteFlowConfig() {
        RuleConfig ruleConfig = new RuleConfig();
        ruleConfig.setRedisPrefix(RuleEngineConfigConsent.STORAGE_REDIS_RULES_PREFIX);
        return ruleConfig;
    }

}
