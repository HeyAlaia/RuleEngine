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

public class CatchCondition extends ConditionParse {


    /**
     * ConditionKey.CATCH_KEY
     * ConditionKey.DO_KEY
     *
     * @param json json
     * @return el
     */
    @Override
    public String buildEl(JSONObject json) {
        JSONObject jsonObject = json.getJSONObject("executableGroup");

        String catchKey = jsonObject.getJSONArray(ConditionKey.CATCH_KEY)
                .stream()
                .map(entry -> {
                    JSONObject entries = JSONUtil.parseObj(entry);
                    return ElParseFactory.buildJsonToEl(entries);
                }).collect(Collectors.joining(", "));

        assert StrUtil.isNotBlank(catchKey);
        catchKey = "CATCH(" + catchKey + ")";

        List<String> list = new ArrayList<>();
        list.add(catchKey);

        JSONArray jsonArray = jsonObject.getJSONArray(ConditionKey.DO_KEY);
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

        return StrUtil.join(".", list);
    }
}
