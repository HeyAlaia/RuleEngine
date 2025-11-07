package com.eport.rule.liteflow.editor.parser.condition;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.eport.rule.liteflow.editor.parser.ConditionParse;
import com.eport.rule.liteflow.editor.parser.ElParseFactory;
import com.yomahub.liteflow.flow.element.condition.ConditionKey;

import java.util.stream.Collectors;

public class AndOrCondition extends ConditionParse {
    @Override
    public String buildEl(JSONObject json) {
        JSONObject jsonObject = json.getJSONObject("executableGroup");

        String AndOrItem = jsonObject.getJSONArray(ConditionKey.AND_OR_ITEM_KEY)
                .stream()
                .map(entry -> {
                    JSONObject entries = JSONUtil.parseObj(entry);
                    return ElParseFactory.buildJsonToEl(entries);
                }).collect(Collectors.joining(", "));
        String booleanConditionType = json.getStr("booleanConditionType");
        assert StrUtil.isNotBlank(booleanConditionType);
        return booleanConditionType + "(" + AndOrItem + ")";
    }
}
