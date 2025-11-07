package traveler.liteflow.out;

import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;
import com.yomahub.liteflow.log.LFLog;
import com.yomahub.liteflow.log.LFLoggerManager;
import jakarta.annotation.Resource;
import traveler.data.OutWarnInfo;
import traveler.data.TravelerInfo;
import traveler.server.ServiceOut;

import java.util.ArrayList;

@LiteflowComponent("CheckRandomParams")
public class CheckRandomParams extends NodeComponent {

    private final LFLog log = LFLoggerManager.getLogger(CheckRandomParams.class);

    @Resource
    private ServiceOut serviceOut;

    @Override
    public boolean isAccess() {
        TravelerInfo context = this.getContextBean(TravelerInfo.class);
        return serviceOut.checkRandom(context);
    }

    @Override
    public void process() {
        log.info("CheckRandomParams executed!");
        ArrayList context = this.getContextBean(ArrayList.class);
        OutWarnInfo outWarnInfo = new OutWarnInfo();
        outWarnInfo.setWarnName("判断普通人员随机数");
        outWarnInfo.setWarnDescription("命中普通人员随机数");
        outWarnInfo.setLevel(98);
        context.add(outWarnInfo);
    }
}