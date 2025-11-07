package traveler.liteflow.enter;

import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeComponent;
import com.yomahub.liteflow.log.LFLog;
import com.yomahub.liteflow.log.LFLoggerManager;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import traveler.data.TravelerInfo;
import traveler.pojo.Traveler;
import traveler.server.ServiceEnter;
import traveler.server.ServiceOut;

@LiteflowComponent("CreateTravelerInfo")
public class CreateTravelerInfo extends NodeComponent {

    private final LFLog log = LFLoggerManager.getLogger(CreateTravelerInfo.class);

    @Resource
    private ServiceEnter serviceEnter;

    @Override
    public void process() {
        log.info("CreateTravelerInfo executed!");
        Traveler traveler = this.getRequestData();
        TravelerInfo travelerByServer = serviceEnter.createTravelerInfo(traveler);
        TravelerInfo context = this.getContextBean(TravelerInfo.class);
        BeanUtils.copyProperties(travelerByServer, context);
    }
}