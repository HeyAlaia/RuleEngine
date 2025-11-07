package traveler.liteflow.enter;

import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;
import com.yomahub.liteflow.log.LFLog;
import com.yomahub.liteflow.log.LFLoggerManager;
import jakarta.annotation.Resource;
import traveler.data.TravelerInfo;
import traveler.server.ServiceEnter;
import traveler.server.ServiceOut;

@LiteflowComponent("CreateTravelerWarnInfo")
public class CreateTravelerWarnInfo extends NodeComponent {

    private final LFLog log = LFLoggerManager.getLogger(CreateTravelerWarnInfo.class);

    @Resource
    private ServiceEnter serviceEnter;

    @Override
    public void process() {
        log.info("CreateTravelerWarnInfo executed!");
        TravelerInfo traveler = this.getContextBean(TravelerInfo.class);
        serviceEnter.createTravelerWarnInfo(traveler);
    }
}