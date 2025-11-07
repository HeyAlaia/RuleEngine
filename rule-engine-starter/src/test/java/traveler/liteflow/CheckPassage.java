package traveler.liteflow;

import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeBooleanComponent;
import com.yomahub.liteflow.log.LFLog;
import com.yomahub.liteflow.log.LFLoggerManager;
import jakarta.annotation.Resource;
import traveler.pojo.Traveler;
import traveler.server.ServiceCommon;
import traveler.server.ServiceOut;

@LiteflowComponent("CheckPassage")
public class CheckPassage extends NodeBooleanComponent {

    private final LFLog log = LFLoggerManager.getLogger(CheckPassage.class);

    @Resource
    private ServiceCommon serviceCommon;

    @Override
    public boolean processBoolean() {
        log.info("CheckPassage executed!");
        Traveler traveler = this.getRequestData();
        return serviceCommon.checkPassage(traveler);
    }
}