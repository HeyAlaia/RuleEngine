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

@LiteflowComponent("checkOtherGoods")
public class checkOtherGoods extends NodeComponent {

    private final LFLog log = LFLoggerManager.getLogger(checkOtherGoods.class);

    @Resource
    private ServiceGoods serviceGoods;

    @Override
    public boolean isAccess() {
        TravelerInfo context = this.getContextBean(TravelerInfo.class);
        return serviceGoods.checkOtherGoods(context);
    }

    @Override
    public void process() {
        log.info("checkSpecialGoods executed!");
        ArrayList context = this.getContextBean(ArrayList.class);
        OutWarnInfo outWarnInfo = new OutWarnInfo();
        outWarnInfo.setWarnName("判断其他...");
        outWarnInfo.setWarnDescription("命中其他...");
        outWarnInfo.setLevel(3);
        context.add(outWarnInfo);
    }
}
