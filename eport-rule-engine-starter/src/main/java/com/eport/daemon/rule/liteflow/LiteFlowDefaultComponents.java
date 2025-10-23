package com.eport.daemon.rule.liteflow;

import com.alibaba.fastjson.JSONObject;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.annotation.LiteflowMethod;
import com.yomahub.liteflow.core.NodeComponent;
import com.yomahub.liteflow.enums.LiteFlowMethodEnum;
import com.yomahub.liteflow.enums.NodeTypeEnum;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: Alaia
 * @Description: LiteFlow测试组件类
 **/
@Slf4j
@LiteflowComponent
public class LiteFlowDefaultComponents {

    @LiteflowMethod(value = LiteFlowMethodEnum.PROCESS, nodeId = "d", nodeName = "D组件", nodeType = NodeTypeEnum.COMMON)
    public void processD(NodeComponent bindCmp) {
        JSONObject requestData = bindCmp.getRequestData();
        log.info("执行折扣计算组件，输入数据: {}", requestData.toJSONString());

        String customerLevel = requestData.getString("customerLevel");
        Double orderAmount = requestData.getDouble("orderAmount");

        double discountRate = 0.0;

        // VIP客户折扣
        if ("VIP".equals(customerLevel)) {
            discountRate += 0.1;
        } else if ("GOLD".equals(customerLevel)) {
            discountRate += 0.05;
        }

        // 订单金额折扣
        if (orderAmount != null) {
            if (orderAmount > 1000) {
                discountRate += 0.05;
            } else if (orderAmount > 500) {
                discountRate += 0.03;
            }
        }

        requestData.put("discountRate", discountRate);
        if (orderAmount != null) {
            double finalAmount = orderAmount * (1 - discountRate);
            requestData.put("finalAmount", finalAmount);
            requestData.put("savedAmount", orderAmount - finalAmount);
        }

        log.info("折扣计算完成，结果: {}", requestData.toJSONString());
    }

}
