package com.eport.rule.liteflow.editor.parser.condition;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.eport.rule.liteflow.editor.parser.ConditionParse;
import com.eport.rule.liteflow.editor.parser.ElParseFactory;
import com.yomahub.liteflow.flow.element.condition.ConditionKey;

import java.util.Objects;
import java.util.stream.Collectors;

public class FinallyCondition extends ConditionParse {
    @Override
    public String buildEl(JSONObject json) {
        JSONObject jsonObject = json.getJSONObject("executableGroup");
        JSONArray jsonArray = jsonObject.getJSONArray(ConditionKey.DEFAULT_KEY);
        String collect = jsonArray.stream().map(entry -> {
            JSONObject entries = JSONUtil.parseObj(entry);
            return ElParseFactory.buildJsonToEl(entries);
        }).filter(Objects::nonNull).collect(Collectors.joining(", "));
        return "FINALLY(" + collect + ")";
    }
}
