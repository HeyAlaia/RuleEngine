package traveler.liteflow.out;

import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeBooleanComponent;
import com.yomahub.liteflow.log.LFLog;
import com.yomahub.liteflow.log.LFLoggerManager;
import jakarta.annotation.Resource;
import traveler.data.TravelerInfo;
import traveler.server.ServiceGoods;

@LiteflowComponent("checkGoods")
public class checkGoods extends NodeBooleanComponent {

    private final LFLog log = LFLoggerManager.getLogger(checkGoods.class);

    @Resource
    private ServiceGoods serviceGoods;

    @Override
    public boolean processBoolean() {
        log.info("checkGoods executed!");
        TravelerInfo context = this.getContextBean(TravelerInfo.class);
        return serviceGoods.checkGoods(context);
    }
}
