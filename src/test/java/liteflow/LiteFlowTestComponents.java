package liteflow;

import com.alibaba.fastjson.JSONObject;
import com.yomahub.liteflow.annotation.LiteflowComponent;
import com.yomahub.liteflow.annotation.LiteflowMethod;
import com.yomahub.liteflow.core.NodeComponent;
import com.yomahub.liteflow.enums.LiteFlowMethodEnum;
import com.yomahub.liteflow.enums.NodeTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Author: Alaia
 * @Description: LiteFlow测试组件类
 **/
@Slf4j
@LiteflowComponent
public class LiteFlowTestComponents {

    @LiteflowMethod(value = LiteFlowMethodEnum.PROCESS, nodeId = "a", nodeName = "A组件", nodeType = NodeTypeEnum.COMMON)
    public void processA(NodeComponent bindCmp) {
        //入参
        JSONObject requestData = bindCmp.getRequestData();
        log.info("执行权重检查组件，输入数据: {}", requestData.toJSONString());
        //上下文
        JSONObject orderContext = bindCmp.getContextBean(JSONObject.class);
        orderContext.put("key", 1);
        Integer weight = requestData.getInteger("weight");
        if (weight != null) {
            if (weight > 100) {
                requestData.put("weightStatus", "heavy");
                requestData.put("shippingFee", 50.0);
            } else if (weight > 50) {
                requestData.put("weightStatus", "medium");
                requestData.put("shippingFee", 30.0);
            } else {
                requestData.put("weightStatus", "light");
                requestData.put("shippingFee", 15.0);
            }
        }

        log.info("权重检查完成，结果: {}", requestData.toJSONString());
    }

    @LiteflowMethod(value = LiteFlowMethodEnum.PROCESS, nodeId = "b", nodeName = "B组件", nodeType = NodeTypeEnum.COMMON)
    public void processB(NodeComponent bindCmp) {
        JSONObject requestData = bindCmp.getRequestData();
        log.info("执行商品检查组件，输入数据: {}", requestData.toJSONString());
        JSONObject orderContext = bindCmp.getContextBean(JSONObject.class);
        log.info("测试上下文，输入数据: {}", orderContext.toJSONString());

        String hasGood = requestData.getString("hasGood");
        String category = requestData.getString("category");

        if ("1".equals(hasGood)) {
            requestData.put("hasGoods", true);
            if ("electronics".equals(category)) {
                requestData.put("needInsurance", true);
                requestData.put("insuranceFee", 20.0);
            } else {
                requestData.put("needInsurance", false);
                requestData.put("insuranceFee", 0.0);
            }
        } else {
            requestData.put("hasGoods", false);
            requestData.put("needInsurance", false);
        }

        log.info("商品检查完成，结果: {}", requestData.toJSONString());
    }

    @LiteflowMethod(value = LiteFlowMethodEnum.PROCESS, nodeId = "c", nodeName = "C组件", nodeType = NodeTypeEnum.COMMON)
    public void processC(NodeComponent bindCmp) {
        JSONObject requestData = bindCmp.getRequestData();
        log.info("执行价格计算组件，输入数据: {}", requestData.toJSONString());

        Double basePrice = requestData.getDouble("price");
        Double shippingFee = requestData.getDouble("shippingFee");
        Double insuranceFee = requestData.getDouble("insuranceFee");

        if (basePrice == null) basePrice = 0.0;
        if (shippingFee == null) shippingFee = 0.0;
        if (insuranceFee == null) insuranceFee = 0.0;

        Double totalPrice = basePrice + shippingFee + insuranceFee;
        requestData.put("totalPrice", totalPrice);

        // 根据总价给予折扣
        if (totalPrice > 500) {
            requestData.put("discount", 0.1);
            requestData.put("finalPrice", totalPrice * 0.9);
        } else if (totalPrice > 200) {
            requestData.put("discount", 0.05);
            requestData.put("finalPrice", totalPrice * 0.95);
        } else {
            requestData.put("discount", 0.0);
            requestData.put("finalPrice", totalPrice);
        }

        log.info("价格计算完成，结果: {}", requestData.toJSONString());
    }

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
