package traveler.server;

import cn.hutool.core.collection.CollectionUtil;
import com.eport.daemon.rule.exception.BusinessLiteFlowException;
import com.yomahub.liteflow.log.LFLog;
import com.yomahub.liteflow.log.LFLoggerManager;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import traveler.constant.TravelerConstant;
import traveler.data.TravelerInfo;
import traveler.pojo.Traveler;

import java.util.ArrayList;

@Service
public class ServiceEnter {

    private final LFLog log = LFLoggerManager.getLogger(ServiceEnter.class);



    public Boolean checkBlacklist(TravelerInfo traveler) {
        String travelerId = traveler.getTravelerId();
        if ("1101051994010974501".equals(travelerId)) {
            traveler.setBlacklist(TravelerConstant.Blacklist.YES);
            return Boolean.TRUE;
        } else {
            traveler.setBlacklist(TravelerConstant.Blacklist.NO);
            return Boolean.FALSE;
        }
    }

    public Boolean checkWriteOff(TravelerInfo traveler) {
        String travelerId = traveler.getTravelerId();
        if ("1101051994010974501".equals(travelerId)) {
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }



    public TravelerInfo createTravelerInfo(Traveler traveler) {
        TravelerInfo build = TravelerInfo.builder()
                .travelerId("110105199401097450")
                .build();
        build.setIdCard("110105199401097450");
        BeanUtils.copyProperties(build, traveler);
        return build;
    }


    public void createTravelerWarnInfo(TravelerInfo traveler) {
        log.info("这里将会写入预警信息");
    }
}
