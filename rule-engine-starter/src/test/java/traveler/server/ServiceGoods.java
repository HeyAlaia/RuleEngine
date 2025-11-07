package traveler.server;

import com.yomahub.liteflow.log.LFLog;
import com.yomahub.liteflow.log.LFLoggerManager;
import org.springframework.stereotype.Service;
import traveler.data.TravelerInfo;

@Service
public class ServiceGoods {

    private final LFLog log = LFLoggerManager.getLogger(ServiceGoods.class);

    public boolean checkGoods(TravelerInfo traveler) {
        String travelerId = traveler.getTravelerId();
        if ("1101051994010974501".equals(travelerId)) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    public boolean checkAmount(TravelerInfo context) {
        return context.getTravelerId().equals("110105199401097450");
    }

    public boolean checkOtherGoods(TravelerInfo context) {
        return context.getTravelerId().equals("110105199401097450");
    }

    public boolean checkSpecialGoods(TravelerInfo context) {
        return context.getTravelerId().equals("110105199401097450");
    }
}
