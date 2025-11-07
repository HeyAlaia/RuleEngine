package traveler.liteflow.enter;

import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeBooleanComponent;
import com.yomahub.liteflow.log.LFLog;
import com.yomahub.liteflow.log.LFLoggerManager;
import jakarta.annotation.Resource;
import traveler.data.TravelerInfo;
import traveler.server.ServiceEnter;
import traveler.server.ServiceOut;

@LiteflowComponent("CheckWriteOff")
public class CheckWriteOff extends NodeBooleanComponent {

    private final LFLog log = LFLoggerManager.getLogger(CheckWriteOff.class);

    @Resource
    private ServiceEnter serviceEnter;

    @Override
    public boolean processBoolean() {
        log.info("CheckWriteOff executed!");
        TravelerInfo context = this.getContextBean(TravelerInfo.class);
        return serviceEnter.checkWriteOff(context);
    }
}