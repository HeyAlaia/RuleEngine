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

public class SwitchCondition extends ConditionParse {

    /**
     * ConditionKey.SWITCH_KEY
     * ConditionKey.SWITCH_TARGET_KEY
     * ConditionKey.SWITCH_DEFAULT_KEY
     *
     * @param json json
     * @return el
     */
    @Override
    public String buildEl(JSONObject json) {
        JSONObject jsonObject = json.getJSONObject("executableGroup");

        String switchKey = jsonObject.getJSONArray(ConditionKey.SWITCH_KEY)
                .stream()
                .map(entry -> {
                    JSONObject entries = JSONUtil.parseObj(entry);
                    return ElParseFactory.buildJsonToEl(entries);
                }).collect(Collectors.joining());

        assert StrUtil.isNotBlank(switchKey);
        switchKey = "SWITCH(" + switchKey + ")";

        String switchTargetKey = jsonObject.getJSONArray(ConditionKey.SWITCH_TARGET_KEY)
                .stream()
                .map(entry -> {
                    JSONObject entries = JSONUtil.parseObj(entry);
                    return ElParseFactory.buildJsonToEl(entries);
                }).collect(Collectors.joining(", "));

        assert StrUtil.isNotBlank(switchTargetKey);
        switchTargetKey = "TO(" + switchTargetKey + ")";

        List<String> list = new ArrayList<>();
        list.add(switchKey);
        list.add(switchTargetKey);

        JSONArray jsonArray = jsonObject.getJSONArray(ConditionKey.SWITCH_DEFAULT_KEY);
        if (jsonArray != null) {
            String switchDefaultKey = jsonArray.stream().map(entry -> {
                JSONObject entries = JSONUtil.parseObj(entry);
                return ElParseFactory.buildJsonToEl(entries);
            }).collect(Collectors.joining(", "));
            if (StrUtil.isNotBlank(switchDefaultKey)) {
                switchDefaultKey = "DEFAULT(" + switchDefaultKey + ")";
                list.add(switchDefaultKey);
            }
        }

        return StrUtil.join(".", list);
    }
}
