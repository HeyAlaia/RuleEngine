package traveler;

import com.eport.rule.EportDaemonRuleEngineApplication;
import com.eport.daemon.rule.common.EngineSourceType;
import com.eport.daemon.rule.common.RuleEngineConfigConsent;
import com.eport.daemon.rule.engine.RuleEngine;
import com.eport.daemon.rule.pojo.RuleConfig;
import com.eport.daemon.rule.utils.RuleEngineHelper;
import com.yomahub.liteflow.enums.ParseModeEnum;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
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

import java.util.ArrayList;

@Slf4j
@SpringBootTest(classes = EportDaemonRuleEngineApplication.class)
@TestPropertySource(locations = { "classpath:application.yml" })
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
class OutTest {

    @Test
    public void testLiteFlowBasic1() {
        log.info("开始测试LiteFlow规则引擎基础功能");

        try {

            //构建引擎
            RuleEngine<Traveler> engine = RuleEngineHelper.bulidRuleEngine(EngineSourceType.DATABASE, getLiteFlowConfig(), TravelerInfo.class, ArrayList.class);
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
                    .direction(TravelerConstant.Direction.OUT)
                    .travelerType(TravelerConstant.TravelerType.TRAVELER)
                    .passageNo("123456")
                    .build();

            log.info("输入: {}", traveler.toString());

            long startTime = System.currentTimeMillis();
            engine.execute("outTraveler", traveler);
            long endTime = System.currentTimeMillis();

            log.info("输出: {}", traveler);
            if (traveler.getPass() == 1) {
                log.info("出区失败");
            } else {
                log.info("出区成功");
            }
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
