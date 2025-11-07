package com.eport.rule.liteflow.editor.parser;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.eport.rule.liteflow.editor.parser.condition.*;
import com.yomahub.liteflow.enums.ExecuteableTypeEnum;

import java.util.HashMap;
import java.util.Map;

import static com.yomahub.liteflow.enums.ConditionTypeEnum.*;

public class ElParseFactory {

    private static final Map<String, ConditionParse> CONDITION_PARESE_MAP = new HashMap<>();

    static {
        CONDITION_PARESE_MAP.put(TYPE_THEN.getType(), new ThenCondition());
        CONDITION_PARESE_MAP.put(TYPE_WHEN.getType(), new WhenCondition());
        CONDITION_PARESE_MAP.put(TYPE_SWITCH.getType(), new SwitchCondition());
        CONDITION_PARESE_MAP.put(TYPE_IF.getType(), new IfCondition());
        CONDITION_PARESE_MAP.put(TYPE_PRE.getType(), new PreCondition());
        CONDITION_PARESE_MAP.put(TYPE_FINALLY.getType(), new FinallyCondition());
        CONDITION_PARESE_MAP.put(TYPE_FOR.getType(), new ForCondition());
        CONDITION_PARESE_MAP.put(TYPE_WHILE.getType(), new WhileCondition());
        CONDITION_PARESE_MAP.put(TYPE_ITERATOR.getType(), new IteratorCondition());
        CONDITION_PARESE_MAP.put(TYPE_CATCH.getType(), new CatchCondition());
        CONDITION_PARESE_MAP.put(TYPE_AND_OR_OPT.getType(), new AndOrCondition());
        CONDITION_PARESE_MAP.put(TYPE_NOT_OPT.getType(), new NotCondition());
    }

    public static String buildJsonToEl(JSONObject json) {
        String executeType = json.getStr("executeType");
        String res = null;
        if (ExecuteableTypeEnum.CONDITION.name().equals(executeType)) {
            String conditionType = json.getStr("conditionType");
            ConditionParse elParse = CONDITION_PARESE_MAP.get(conditionType);
            res = elParse.buildEl(json);
        } else if (ExecuteableTypeEnum.NODE.name().equals(executeType)) {
            res = NodeParse.buildEl(json);
        } else if (ExecuteableTypeEnum.CHAIN.name().equals(executeType)) {

        }
        String tag = json.getStr("tag");
        if (StrUtil.isNotBlank(res) && StrUtil.isNotBlank(tag)) {
            res = res + ".tag(\"" + tag + "\")";
        }
        return res;
    }

}
