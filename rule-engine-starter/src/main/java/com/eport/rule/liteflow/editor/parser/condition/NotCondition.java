package com.eport.rule.liteflow.editor.parser.condition;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.eport.rule.liteflow.editor.parser.ConditionParse;
import com.eport.rule.liteflow.editor.parser.ElParseFactory;
import com.yomahub.liteflow.flow.element.condition.ConditionKey;

import java.util.stream.Collectors;

public class NotCondition extends ConditionParse {
    @Override
    public String buildEl(JSONObject json) {
        JSONObject jsonObject = json.getJSONObject("executableGroup");

        String AndOrItem = jsonObject.getJSONArray(ConditionKey.NOT_ITEM_KEY)
                .stream()
                .map(entry -> {
                    JSONObject entries = JSONUtil.parseObj(entry);
                    return ElParseFactory.buildJsonToEl(entries);
                }).collect(Collectors.joining());

        return "NOT(" + AndOrItem + ")";
    }
}
