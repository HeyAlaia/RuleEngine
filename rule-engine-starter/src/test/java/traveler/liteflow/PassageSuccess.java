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

@LiteflowComponent("PassageSuccess")
public class PassageSuccess extends NodeComponent {

    private final LFLog log = LFLoggerManager.getLogger(PassageSuccess.class);

    @Resource
    private ServiceCommon serviceCommon;

    @Override
    public void process() {
        log.info("PassageSuccess executed!");
        Traveler traveler = this.getRequestData();
        traveler.setPass(TravelerConstant.Passage.SUCCESS);
        TravelerInfo contextBean = this.getContextBean(TravelerInfo.class);
        log.info("PassageSuccess executed! bean = {}", contextBean);
        serviceCommon.setSuccessLog(contextBean);
        this.setIsEnd(true);
    }
}