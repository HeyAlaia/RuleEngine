package traveler.liteflow.out;

import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeBooleanComponent;
import com.yomahub.liteflow.log.LFLog;
import com.yomahub.liteflow.log.LFLoggerManager;
import jakarta.annotation.Resource;
import traveler.server.ServiceOut;

import java.util.ArrayList;

@LiteflowComponent("HasWarnInfo")
public class HasWarnInfo extends NodeBooleanComponent {

    private final LFLog log = LFLoggerManager.getLogger(HasWarnInfo.class);

    @Resource
    private ServiceOut serviceOut;

    @Override
    public boolean processBoolean() {
        log.info("HasWarnInfo executed!");
        ArrayList context = this.getContextBean(ArrayList.class);
        return serviceOut.checkHasWarnInfo(context);
    }
}
