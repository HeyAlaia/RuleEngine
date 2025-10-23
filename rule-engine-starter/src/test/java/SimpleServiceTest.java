import com.alibaba.fastjson.JSONObject;
import com.eport.daemon.rule.EportDaemonRuleEngineApplication;
import com.eport.daemon.rule.common.EngineSourceType;
import com.eport.daemon.rule.common.EngineType;
import com.eport.daemon.rule.common.RuleEngineConfigConsent;
import com.eport.daemon.rule.engine.RuleEngine;
import com.eport.daemon.rule.engine.RuleEngineBuilder;
import com.eport.daemon.rule.pojo.RuleConfig;
import com.eport.daemon.rule.utils.RuleEngineHelper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@SpringBootTest(classes = EportDaemonRuleEngineApplication.class)
@TestPropertySource(locations = {"classpath:application.yml"})
public class SimpleServiceTest {

    @Test
    public void testPull() {
        RuleEngine<JSONObject> engine = RuleEngineHelper.bulidRuleEngine(EngineType.DROOLS, EngineSourceType.LOCAL, getLiteFlowConfig());

        //测试数据
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("text", "XY4A753");
        jsonObject.put("weight", 59);
        jsonObject.put("hasGood", "1");
        System.out.println("因素: " + jsonObject.toJSONString());
        //规则执行
        int index = 0;
        long startTime = System.currentTimeMillis();
        while (index < 1) {
            engine.execute(EngineSourceType.LOCAL + "ruleset1", jsonObject);
            System.out.println("执行结果: " + jsonObject.toJSONString());
            index++;
        }
        long endTime = System.currentTimeMillis();
        System.out.println("执行总耗时: " + (endTime - startTime) / 1000.0 + " s");
    }

    private static RuleConfig getLiteFlowConfig() {
        RuleConfig ruleConfig = new RuleConfig();
        ruleConfig.setDroolsDateformat("yyyy-MM-dd HH:mm:ss");
        return ruleConfig;
    }
}

