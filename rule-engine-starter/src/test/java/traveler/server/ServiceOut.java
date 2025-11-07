package traveler.server;

import cn.hutool.core.collection.CollectionUtil;
import com.yomahub.liteflow.log.LFLog;
import com.yomahub.liteflow.log.LFLoggerManager;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import traveler.constant.TravelerConstant;
import traveler.data.TravelerInfo;
import traveler.pojo.Traveler;

import java.util.ArrayList;

@Service
public class ServiceOut {

    private final LFLog log = LFLoggerManager.getLogger(ServiceOut.class);


    public boolean checkAlreadyEnter(TravelerInfo traveler) {
        String travelerId = traveler.getTravelerId();
        if ("110105199401097450".equals(travelerId)) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    public boolean checkHasWarnInfo(ArrayList context) {
        log.info("这里将会写入预警执行信息");
        log.info("当前命中异常条数: {}, 异常内容: {}", context.size(), context);
        return CollectionUtil.isNotEmpty(context);
    }

    public boolean checkKeyPersonParams(TravelerInfo traveler) {
        String travelerId = traveler.getTravelerId();
        if ("110105199401097450".equals(travelerId)) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    public boolean checkRandom(TravelerInfo traveler) {
        String travelerId = traveler.getTravelerId();
        if ("110105199401097450".equals(travelerId)) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }
}
