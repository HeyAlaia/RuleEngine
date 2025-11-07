package com.eport.rule.liteflow.editor.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.eport.common.security.annotation.Inner;
import com.eport.rule.liteflow.editor.service.LiteFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rule")
public class LiteFlowController {

    @Autowired
    private LiteFlowService liteFlowService;

    @PostMapping("/validateEl")
    public void validateEl(@RequestBody JSONObject obj) {
        String el = obj.getStr("el");
        JSONArray nodes = obj.getJSONArray("nodes");
        liteFlowService.validateEl(el, nodes);
    }

    @PostMapping("/buildElToJson")
    public Object buildElToJson(@RequestBody JSONObject obj) {
        String el = obj.getStr("el");
        JSONArray nodes = obj.getJSONArray("nodes");
        return liteFlowService.buildJson(el, nodes);
    }

    @PostMapping("/buildJsonToEl")
    public Object buildJsonToEl(@RequestBody JSONObject obj) {
        return liteFlowService.buildEl(obj);
    }


}
