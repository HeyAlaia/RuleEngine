package com.eport.rule.liteflow.editor.service;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;

public interface LiteFlowService {

    /**
     * 校验El
     *
     * @param el el
     */
    void validateEl(String el, JSONArray nodes);

    /**
     * ElTOJson
     *
     * @param el el
     */
    JSONObject buildJson(String el, JSONArray nodes);

    /**
     * JsonTOEl
     *
     * @param json el
     */
    String buildEl(JSONObject json);
}
