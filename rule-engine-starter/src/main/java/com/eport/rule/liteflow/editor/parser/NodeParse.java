package com.eport.rule.liteflow.editor.parser;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.yomahub.liteflow.flow.element.Node;

public class NodeParse {

    public static JSONObject toJson(Node node) {
        Object cmpData = node.getCmpData();
        if(ObjUtil.isNotEmpty(cmpData) && JSONUtil.isTypeJSON(cmpData.toString())){
            cmpData = JSONUtil.parseObj(cmpData);
        }

        return new JSONObject().set("id", node.getId())
                .set("name", node.getName())
                .set("clazz", node.getClazz())
                .set("type", node.getType().getCode())
                .set("script", node.getScript())
                .set("language", node.getLanguage())
                .set("tag", node.getTag())
                .set("cmpData", cmpData)
                .set("executeType", node.getExecuteType().name());
    }

    public static String buildEl(JSONObject json) {
        String nodeName = json.getStr("id");
        String cmpData = json.getStr("cmpData");
        if (StrUtil.isNotBlank(cmpData)) {
            nodeName = nodeName + ".data('" + cmpData + "')";
        }
        return nodeName;
    }
}
