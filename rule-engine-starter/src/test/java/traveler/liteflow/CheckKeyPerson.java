package traveler.liteflow;

import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeBooleanComponent;
import com.yomahub.liteflow.log.LFLog;
import com.yomahub.liteflow.log.LFLoggerManager;
import jakarta.annotation.Resource;
import traveler.data.TravelerInfo;
import traveler.server.ServiceCommon;
import traveler.server.ServiceOut;

@LiteflowComponent("CheckKeyPerson")
public class CheckKeyPerson extends NodeBooleanComponent {

    private final LFLog log = LFLoggerManager.getLogger(CheckKeyPerson.class);

    @Resource
    private ServiceCommon serviceCommon;

    @Override
    public boolean processBoolean() {
        log.info("CheckKeyPerson executed!");
        TravelerInfo context = this.getContextBean(TravelerInfo.class);
        return serviceCommon.checkKeyPerson(context);
    }
}