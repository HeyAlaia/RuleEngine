package traveler.liteflow.out;

import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeBooleanComponent;
import com.yomahub.liteflow.log.LFLog;
import com.yomahub.liteflow.log.LFLoggerManager;
import jakarta.annotation.Resource;
import traveler.data.TravelerInfo;
import traveler.server.ServiceOut;

@LiteflowComponent("alreadyEnter")
public class alreadyEnter extends NodeBooleanComponent {

    private final LFLog log = LFLoggerManager.getLogger(alreadyEnter.class);

    @Resource
    private ServiceOut serviceOut;

    @Override
    public boolean processBoolean() {
        log.info("alreadyEnter executed!");
        TravelerInfo context = this.getContextBean(TravelerInfo.class);
        return serviceOut.checkAlreadyEnter(context);
    }
}
