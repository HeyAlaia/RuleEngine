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

public class IfCondition extends ConditionParse {

    /**
     * ConditionKey.IF_KEY
     * ConditionKey.IF_FALSE_CASE_KEY
     * ConditionKey.IF_TRUE_CASE_KEY
     *
     * @param json json
     * @return el
     */
    @Override
    public String buildEl(JSONObject json) {
        JSONObject jsonObject = json.getJSONObject("executableGroup");

        // 判断是否有非bool的语法节点
        // 最终只会有一个节点 true or false
        String ifKey = jsonObject.getJSONArray(ConditionKey.IF_KEY)
                .stream()
                .map(entry -> {
                    JSONObject entries = JSONUtil.parseObj(entry);
                    return ElParseFactory.buildJsonToEl(entries);
                }).collect(Collectors.joining(", "));

        assert StrUtil.isNotBlank(ifKey);

        String trueKey = jsonObject.getJSONArray(ConditionKey.IF_TRUE_CASE_KEY).stream().map(entry -> {
            JSONObject entries = JSONUtil.parseObj(entry);
            return ElParseFactory.buildJsonToEl(entries);
        }).collect(Collectors.joining());

        assert StrUtil.isNotBlank(trueKey);

        List<String> list = new ArrayList<>();
        list.add(ifKey);
        list.add(trueKey);

        JSONArray jsonArray = jsonObject.getJSONArray(ConditionKey.IF_FALSE_CASE_KEY);
        if (jsonArray != null) {
            String falseKey = jsonArray.stream().map(entry -> {
                JSONObject entries = JSONUtil.parseObj(entry);
                return ElParseFactory.buildJsonToEl(entries);
            }).collect(Collectors.joining());
            if (StrUtil.isNotBlank(falseKey)) {
                list.add(falseKey);
            }
        }
        String join = StrUtil.join(", ", list);
        return "IF(" + join + ")";
    }
}
