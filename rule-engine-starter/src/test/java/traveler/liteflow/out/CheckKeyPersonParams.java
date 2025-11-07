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

@LiteflowComponent("CheckKeyPersonParams")
public class CheckKeyPersonParams extends NodeComponent {

    private final LFLog log = LFLoggerManager.getLogger(CheckKeyPersonParams.class);

    @Resource
    private ServiceOut serviceOut;

    @Override
    public boolean isAccess() {
        TravelerInfo context = this.getContextBean(TravelerInfo.class);
        return serviceOut.checkKeyPersonParams(context);
    }

    @Override
    public void process() {
        log.info("CheckKeyPersonParams executed!");
        ArrayList context = this.getContextBean(ArrayList.class);
        OutWarnInfo outWarnInfo = new OutWarnInfo();
        outWarnInfo.setWarnName("判断重点人员随机数");
        outWarnInfo.setWarnDescription("命中重点人员随机数");
        outWarnInfo.setLevel(99);
        context.add(outWarnInfo);
    }
}