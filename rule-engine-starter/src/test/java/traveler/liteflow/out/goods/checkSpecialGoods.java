package traveler.liteflow.out.goods;

import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;
import com.yomahub.liteflow.log.LFLog;
import com.yomahub.liteflow.log.LFLoggerManager;
import jakarta.annotation.Resource;
import traveler.data.OutWarnInfo;
import traveler.data.TravelerInfo;
import traveler.server.ServiceGoods;

import java.util.ArrayList;

@LiteflowComponent("checkSpecialGoods")
public class checkSpecialGoods extends NodeComponent {

    private final LFLog log = LFLoggerManager.getLogger(checkSpecialGoods.class);

    @Resource
    private ServiceGoods serviceGoods;

    @Override
    public boolean isAccess() {
        TravelerInfo context = this.getContextBean(TravelerInfo.class);
        return serviceGoods.checkSpecialGoods(context);
    }

    @Override
    public void process() {
        log.info("checkSpecialGoods executed!");
        ArrayList context = this.getContextBean(ArrayList.class);
        OutWarnInfo outWarnInfo = new OutWarnInfo();
        outWarnInfo.setWarnName("判断违禁品数量");
        outWarnInfo.setWarnDescription("命中判断违禁品数量");
        outWarnInfo.setLevel(2);
        context.add(outWarnInfo);
    }
}
