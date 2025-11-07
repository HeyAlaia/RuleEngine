package traveler.liteflow.enter;

import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeBooleanComponent;
import com.yomahub.liteflow.log.LFLog;
import com.yomahub.liteflow.log.LFLoggerManager;
import jakarta.annotation.Resource;
import traveler.data.TravelerInfo;
import traveler.server.ServiceEnter;
import traveler.server.ServiceOut;

@LiteflowComponent("CheckBlacklist")
public class CheckBlacklist extends NodeBooleanComponent {

    private final LFLog log = LFLoggerManager.getLogger(CheckBlacklist.class);


    @Resource
    private ServiceEnter serviceEnter;

    @Override
    public boolean processBoolean() {
        log.info("CheckBlacklist executed!");
        TravelerInfo context = this.getContextBean(TravelerInfo.class);
        return serviceEnter.checkBlacklist(context);
    }
}