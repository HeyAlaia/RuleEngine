package traveler.liteflow;

import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;
import com.yomahub.liteflow.log.LFLog;
import com.yomahub.liteflow.log.LFLoggerManager;
import jakarta.annotation.Resource;
import traveler.constant.TravelerConstant;
import traveler.data.TravelerInfo;
import traveler.pojo.Traveler;
import traveler.server.ServiceCommon;
import traveler.server.ServiceOut;

@LiteflowComponent("PassageFail")
public class PassageFail extends NodeComponent {

    private final LFLog log = LFLoggerManager.getLogger(PassageFail.class);

    @Resource
    private ServiceCommon serviceCommon;

    @Override
    public void process() {
        log.info("PassageFail executed!");
        Traveler traveler = this.getRequestData();
        traveler.setPass(TravelerConstant.Passage.FAIL);
        TravelerInfo contextBean = this.getContextBean(TravelerInfo.class);
        log.info("PassageFail executed! bean = {}", contextBean);
        serviceCommon.setFailLog(contextBean);
        this.setIsEnd(true);
    }
}