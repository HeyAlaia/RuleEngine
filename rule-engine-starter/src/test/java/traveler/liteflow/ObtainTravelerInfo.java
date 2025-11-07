package traveler.liteflow;

import cn.hutool.core.util.StrUtil;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.core.NodeBooleanComponent;
import com.yomahub.liteflow.log.LFLog;
import com.yomahub.liteflow.log.LFLoggerManager;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import traveler.pojo.Traveler;
import traveler.data.TravelerInfo;
import traveler.server.ServiceCommon;
import traveler.server.ServiceOut;

import java.util.Objects;

@LiteflowComponent("ObtainTravelerInfo")
public class ObtainTravelerInfo extends NodeBooleanComponent {

    private final LFLog log = LFLoggerManager.getLogger(ObtainTravelerInfo.class);

    @Resource
    private ServiceCommon serviceCommon;

    @Override
    public boolean processBoolean() {
        log.info("ObtainTravelerInfo executed!");
        Traveler traveler = this.getRequestData();
        TravelerInfo travelerByServer = serviceCommon.getTransfer(traveler);
        boolean blank = Objects.nonNull(travelerByServer) && StrUtil.isNotBlank(travelerByServer.getTravelerId());
        if (blank) {
            TravelerInfo context = this.getContextBean(TravelerInfo.class);
            BeanUtils.copyProperties(travelerByServer, context);
        }
        return blank;
    }
}