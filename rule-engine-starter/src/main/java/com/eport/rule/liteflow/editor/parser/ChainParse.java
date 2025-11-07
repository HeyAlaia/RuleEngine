package com.eport.rule.liteflow.editor.parser;

import cn.hutool.json.JSONObject;
import com.yomahub.liteflow.flow.element.Chain;

public class ChainParse {
    public static JSONObject toJson(Chain chain) {
        return new JSONObject().set("chainId", chain.getChainId())
                .set("conditionList", chain.getConditionList())
                .set("el", chain.getEl())
                .set("executeType", chain.getExecuteType().name());
    }
}
