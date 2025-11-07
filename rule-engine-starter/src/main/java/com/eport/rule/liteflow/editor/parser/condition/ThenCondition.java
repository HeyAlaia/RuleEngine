package com.eport.rule.liteflow.editor.parser.condition;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.eport.rule.liteflow.editor.parser.ConditionParse;
import com.eport.rule.liteflow.editor.parser.ElParseFactory;
import com.yomahub.liteflow.flow.element.condition.ConditionKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ThenCondition extends ConditionParse {

    /**
     * ConditionKey.PRE_KEY
     * ConditionKey.FINALLY_KEY
     * ConditionKey.DEFAULT_KEY
     *
     * @param json json
     * @return el
     */
    @Override
    public String buildEl(JSONObject json) {
        JSONObject jsonObject = json.getJSONObject("executableGroup");

        List<String> list = new ArrayList<>();

        JSONArray preJsonArray = jsonObject.getJSONArray(ConditionKey.PRE_KEY);
        if (CollUtil.isNotEmpty(preJsonArray)) {
            String preEL = preJsonArray.stream().map(entry -> {
                JSONObject entries = JSONUtil.parseObj(entry);
                return ElParseFactory.buildJsonToEl(entries);
            }).filter(Objects::nonNull).collect(Collectors.joining(", "));
            list.add(preEL);
        }

        JSONArray jsonArray = jsonObject.getJSONArray(ConditionKey.DEFAULT_KEY);
        String collect = jsonArray.stream().map(entry -> {
            JSONObject entries = JSONUtil.parseObj(entry);
            return ElParseFactory.buildJsonToEl(entries);
        }).filter(Objects::nonNull).collect(Collectors.joining(", "));
        list.add(collect);

        JSONArray finallyJsonArray = jsonObject.getJSONArray(ConditionKey.FINALLY_KEY);
        if (CollUtil.isNotEmpty(finallyJsonArray)) {
            String finallyEL = finallyJsonArray.stream().map(entry -> {
                JSONObject entries = JSONUtil.parseObj(entry);
                return ElParseFactory.buildJsonToEl(entries);
            }).filter(Objects::nonNull).collect(Collectors.joining(", "));
            list.add(finallyEL);
        }


        return "THEN(" + CollUtil.join(list, ", ") + ")";
    }
}
