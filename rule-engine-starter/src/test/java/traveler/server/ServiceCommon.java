package traveler.server;

import com.yomahub.liteflow.log.LFLog;
import com.yomahub.liteflow.log.LFLoggerManager;
import org.springframework.stereotype.Service;
import traveler.constant.TravelerConstant;
import traveler.data.TravelerInfo;
import traveler.pojo.Traveler;

@Service
public class ServiceCommon {

    private final LFLog log = LFLoggerManager.getLogger(ServiceGoods.class);

    public void setFailLog(TravelerInfo traveler) {
        log.info("这里就将会写入失败日志, 方向是: {}", traveler.getDirection());
    }

    public void setSuccessLog(TravelerInfo traveler) {
        log.info("这里就将会写入成功日志, 方向是: {}", traveler.getDirection());
    }

    public TravelerInfo getTransfer(Traveler traveler) {
        Integer cardType = traveler.getCardType();
        String cardNumber = traveler.getCardNumber();
        String direction = traveler.getDirection();
        if (cardType == 0 && "110105199401097450".equals(cardNumber)) {
            return TravelerInfo.builder()
                    .name("测试")
                    .namePinyin("test")
                    .sex(1)
                    .birthday("1994-01-09")
                    .country("中国")
                    .cardType(0)
                    .cardNumber("110105199401097450")
                    .picture("image:test")
                    .direction(direction)
                    .travelerType(TravelerConstant.TravelerType.TRAVELER)
                    .passageNo("123456")
                    .travelerId("110105199401097450")
                    .idCard("110105199401097450")
                    .build();
        } else {
            return null;
        }
    }

    public boolean checkPassage(Traveler traveler) {
        String passageNo = traveler.getPassageNo();
        String travelerType = traveler.getTravelerType();
        return "123456".equals(passageNo) && TravelerConstant.TravelerType.TRAVELER.equals(travelerType);
    }


    public boolean checkKeyPerson(TravelerInfo context) {
        String travelerId = context.getTravelerId();
        if ("110105199401097450".equals(travelerId)) {
            context.setKeyPerson(TravelerConstant.KeyPerson.YES);
            return Boolean.TRUE;
        } else {
            context.setKeyPerson(TravelerConstant.KeyPerson.NO);
            return Boolean.FALSE;
        }
    }
}
