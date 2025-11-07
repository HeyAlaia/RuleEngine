package traveler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.eport.daemon.rule.common.EngineSourceType;
import com.eport.daemon.rule.engine.RuleEngine;
import com.eport.daemon.rule.pojo.RuleConfig;
import com.eport.daemon.rule.utils.RuleEngineHelper;
import com.eport.rule.EportDaemonRuleEngineApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.enums.ParseModeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import traveler.constant.TravelerConstant;
import traveler.data.TravelerInfo;
import traveler.liteflow.*;
import traveler.liteflow.enter.CheckBlacklist;
import traveler.liteflow.enter.CheckWriteOff;
import traveler.liteflow.enter.CreateTravelerInfo;
import traveler.liteflow.enter.CreateTravelerWarnInfo;
import traveler.liteflow.out.*;
import traveler.liteflow.out.goods.checkAmount;
import traveler.liteflow.out.goods.checkOtherGoods;
import traveler.liteflow.out.goods.checkSpecialGoods;
import traveler.pojo.Traveler;
import traveler.server.ServiceCommon;
import traveler.server.ServiceEnter;
import traveler.server.ServiceGoods;
import traveler.server.ServiceOut;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@SpringBootTest(classes = EportDaemonRuleEngineApplication.class)
@TestPropertySource(locations = {"classpath:application.yml"})
@Import(value = {CheckBlacklist.class
        , CheckKeyPerson.class
        , CheckPassage.class
        , CheckWriteOff.class
        , CreateTravelerInfo.class
        , CreateTravelerWarnInfo.class
        , ObtainTravelerInfo.class
        , PassageFail.class
        , PassageSuccess.class
        , ServiceOut.class
        , ServiceGoods.class
        , ServiceCommon.class
        , ServiceEnter.class
        , CheckRandomParams.class
        , CheckKeyPersonParams.class
        , HasWarnInfo.class
        , checkGoods.class
        , alreadyEnter.class
        , checkSpecialGoods.class
        , checkOtherGoods.class
        , checkAmount.class
})
public class EnterTest {

    @Resource
    private ApplicationContext applicationContext;

    @Test
    public void getSpringBeans(){
        JSONArray jsonArray = new JSONArray();
        Map<String, Object> liteflowBeans = applicationContext.getBeansWithAnnotation(LiteflowComponent.class);
        if (liteflowBeans.isEmpty()) {
            log.info("没有找到任何标记了 @LiteflowComponent 的Bean。");
        } else {
            liteflowBeans.forEach((beanName, beanInstance) -> {
                jsonArray.add(generateLiteflowComponentJson(beanInstance.getClass()));
            });
        }
        JSONObject mainJson = new JSONObject();
        mainJson.put("nodes", jsonArray);
        JSONArray modelsJson = new JSONArray();
        JSONObject models = new JSONObject();
        models.put("label", "默认节点");
        models.put("model", "default");
        models.put("description", "当未进行初始化时使用默认节点");
        modelsJson.add(models);
        mainJson.put("models", modelsJson);
        log.info(mainJson.toJSONString());
    }
    public static JSONObject generateLiteflowComponentJson(Class<?> componentClass) {
        JSONObject jsonMap = new JSONObject();
        // 1. 获取 id (从 @LiteflowComponent 注解)
        LiteflowComponent liteflowAnno = componentClass.getAnnotation(LiteflowComponent.class);
        if (liteflowAnno != null && !liteflowAnno.value().isEmpty()) {
            jsonMap.put("id", liteflowAnno.value());
            jsonMap.put("label", liteflowAnno.value()); // 默认label与id相同
        } else {
            jsonMap.put("id", componentClass.getSimpleName());
            jsonMap.put("label", componentClass.getSimpleName());
        }
        // 2. 获取 type (根据父类判断)
        String parentClassName = componentClass.getSuperclass().getSimpleName();
        String type = determineLiteflowNodeType(parentClassName);
        jsonMap.put("type", type); // 自定义方法根据父类名判断type

        // 3. 默认值
        jsonMap.put("model", "default");

        // 4. description
        jsonMap.put("description", String.format("LiteFlow 组件 %s，继承自 %s，属于%s组件。",
                componentClass.getName(),
                parentClassName,
                type));
        return jsonMap;
    }
    // 根据LiteFlow的父类组件名称来推断节点类型
    private static String determineLiteflowNodeType(String parentClassName) {
        switch (parentClassName) {
            case "NodeComponent":
                return "common";
            case "NodeSwitchComponent":
                return "switch";
            case "NodeBooleanComponent":
                return "boolean";
            case "NodeForComponent":
                return "for";
            case "NodeIteratorComponent":
                return "iterator";
            default:
                return "unknown";
        }
    }

    @Test
    public void EnterTest(){
        long startTime = System.currentTimeMillis();
        this.testLiteFlowBasic1();
        long endTime = System.currentTimeMillis();
        log.info("执行耗时: {} ms", endTime - startTime);
    }

    @Test
    public void testLiteFlowBasic1() {
        log.info("开始测试LiteFlow规则引擎基础功能");

        try {
            long startTime = System.currentTimeMillis();
            //构建引擎
            RuleEngine<Traveler> engine = RuleEngineHelper.bulidRuleEngine(EngineSourceType.DATABASE, getLiteFlowConfig(), TravelerInfo.class);
            //身份证号码:110105199401097450,出生日期:1994-01-09,性别:男,年龄:31,出生地:北京市 北京市辖区 朝阳区
            Traveler traveler = Traveler.builder()
                    .name("测试")
                    .namePinyin("test")
                    .sex(1)
                    .birthday("1994-01-09")
                    .country("中国")
                    .cardType(0)
                    .cardNumber("110105199401097450")
                    .picture("image:test")
                    .direction(TravelerConstant.Direction.ENTER)
                    .travelerType(TravelerConstant.TravelerType.TRAVELER)
                    .passageNo("123456")
                    .build();

            log.info("输入: {}", traveler.toString());
            engine.execute("enterTraveler", traveler);

            log.info("输出: {}", traveler);
            if (traveler.getPass() == 1) {
                log.info("入区失败");
            } else {
                log.info("入区成功");
            }
            long endTime = System.currentTimeMillis();
            log.info("执行耗时: {} ms", endTime - startTime);
            engine.close();
            log.info("LiteFlow基础功能测试完成");
        } catch (Exception e) {
            log.error("LiteFlow测试过程中发生错误: {}", e.getMessage(), e);
            throw e;
        }
    }

    private static RuleConfig getLiteFlowConfig() {
        RuleConfig ruleConfig = new RuleConfig();
        ruleConfig.setParseModeEnum(ParseModeEnum.PARSE_ONE_ON_FIRST_EXEC);
        return ruleConfig;
    }

}
