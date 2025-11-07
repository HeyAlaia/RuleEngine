package com.eport.rule.liteflow.editor.parser.condition;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.eport.rule.liteflow.editor.parser.ConditionParse;
import com.eport.rule.liteflow.editor.parser.ElParseFactory;
import com.yomahub.liteflow.flow.element.condition.ConditionKey;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WhileCondition extends ConditionParse {


    /**
     * ConditionKey.FOR_KEY
     * ConditionKey.DO_KEY
     *
     * @param json json
     * @return el
     */
    @Override
    public String buildEl(JSONObject json) {
        JSONObject execGroup = json.getJSONObject("executableGroup");

        // 最终只会有一个节点
        String whileKey = execGroup.getJSONArray(ConditionKey.WHILE_KEY)
                .stream()
                .map(entry -> {
                    JSONObject entries = JSONUtil.parseObj(entry);
                    return ElParseFactory.buildJsonToEl(entries);
                }).collect(Collectors.joining(", "));

        assert StrUtil.isNotBlank(whileKey);
        whileKey = "WHILE(" + whileKey + ")";
        List<String> list = new ArrayList<>();
        list.add(whileKey);

        JSONArray jsonArray = execGroup.getJSONArray(ConditionKey.DO_KEY);
        if (jsonArray != null) {
            String doKey = jsonArray.stream().map(entry -> {
                JSONObject entries = JSONUtil.parseObj(entry);
                return ElParseFactory.buildJsonToEl(entries);
            }).collect(Collectors.joining(", "));
            if (StrUtil.isNotBlank(doKey)) {
                doKey = "DO(" + doKey + ")";
                list.add(doKey);
            }
        }

        JSONArray jsonBreakArray = execGroup.getJSONArray(ConditionKey.BREAK_KEY);
        if (jsonBreakArray != null) {
            String breakKey = jsonBreakArray.stream().map(entry -> {
                JSONObject entries = JSONUtil.parseObj(entry);
                return ElParseFactory.buildJsonToEl(entries);
            }).collect(Collectors.joining(", "));
            if (StrUtil.isNotBlank(breakKey)) {
                breakKey = "BREAK(" + breakKey + ")";
                list.add(breakKey);
            }
        }

        return StrUtil.join(".", list);
    }
}
