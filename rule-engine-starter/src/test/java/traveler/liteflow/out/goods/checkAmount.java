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

@LiteflowComponent("checkAmount")
public class checkAmount extends NodeComponent {

    private final LFLog log = LFLoggerManager.getLogger(checkAmount.class);

    @Resource
    private ServiceGoods serviceGoods;

    @Override
    public boolean isAccess() {
        TravelerInfo context = this.getContextBean(TravelerInfo.class);
        return serviceGoods.checkAmount(context);
    }

    @Override
    public void process() {
        log.info("checkAmount executed!");
        ArrayList context = this.getContextBean(ArrayList.class);
        OutWarnInfo outWarnInfo = new OutWarnInfo();
        outWarnInfo.setWarnName("判断购物金额");
        outWarnInfo.setWarnDescription("命中判断购物金额");
        outWarnInfo.setLevel(1);
        context.add(outWarnInfo);
    }
}
