package com.eport.rule.liteflow.editor.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.eport.rule.liteflow.editor.parser.ChainParse;
import com.eport.rule.liteflow.editor.parser.ElParseFactory;
import com.eport.rule.liteflow.editor.parser.NodeParse;
import com.eport.rule.liteflow.editor.service.LiteFlowService;
import com.ql.util.express.DefaultContext;
import com.ql.util.express.InstructionSet;
import com.ql.util.express.exception.QLException;
import com.yomahub.liteflow.enums.ExecuteableTypeEnum;
import com.yomahub.liteflow.enums.NodeTypeEnum;
import com.yomahub.liteflow.exception.DataNotFoundException;
import com.yomahub.liteflow.exception.ELParseException;
import com.yomahub.liteflow.exception.FlowSystemException;
import com.yomahub.liteflow.flow.FlowBus;
import com.yomahub.liteflow.flow.element.Chain;
import com.yomahub.liteflow.flow.element.Condition;
import com.yomahub.liteflow.flow.element.Executable;
import com.yomahub.liteflow.flow.element.Node;
import com.yomahub.liteflow.flow.element.condition.AndOrCondition;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.yomahub.liteflow.builder.el.LiteFlowChainELBuilder.EXPRESS_RUNNER;

@Slf4j
@Service
public class LiteFlowServiceImpl implements LiteFlowService {


    @Override
    public void validateEl(String el, JSONArray nodes) {
        buildEL(el, nodes);
    }

    @Override
    public JSONObject buildJson(String elInfo, JSONArray nodes)  {
        Condition condition = buildEL(elInfo, nodes);
        return buildJsonCondition(condition);
    }

    @Override
    public String buildEl(JSONObject json) {
        try {
            return ElParseFactory.buildJsonToEl(json);
        } catch (Exception e) {
            log.error("构建el异常", e);
        }
        return "";
    }

    private JSONObject buildJsonCondition(Executable executable) {
        ExecuteableTypeEnum executeType = executable.getExecuteType();
        if (executeType == ExecuteableTypeEnum.CONDITION) {
            JSONObject res = new JSONObject();
            Condition cnd = (Condition) executable;
            Map<String, List<JSONObject>> executableGroupJson = cnd.getExecutableGroup().entrySet().stream().collect(Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> entry.getValue().stream()
                            .map(this::buildJsonCondition) // 将每个 Executable 转换为 JSONObject
                            .collect(Collectors.toList())));
            // bool节点独有
            if (executable instanceof AndOrCondition) {
                res.set("booleanConditionType", ((AndOrCondition) cnd).getBooleanConditionType());
            }
            res.set("id", cnd.getId());
            res.set("tag", cnd.getTag());
            res.set("conditionType", cnd.getConditionType().getType());
            res.set("executeType", executeType.name());
            res.set("executableGroup", executableGroupJson);
            return res;
        } else if (executeType == ExecuteableTypeEnum.NODE) {
            Node cnd = (Node) executable;
            return NodeParse.toJson(cnd);
        } else if (executeType == ExecuteableTypeEnum.CHAIN) {
            Chain cnd = (Chain) executable;
            return ChainParse.toJson(cnd);
        }
        return null;
    }

    private Condition buildEL(String elStr, JSONArray nodes) {
        if (StrUtil.isBlank(elStr)) {
            String errMsg = StrUtil.format("no el");
            throw new FlowSystemException(errMsg);
        }

        List<String> errorList = new ArrayList<>();
        try {
            DefaultContext<String, Object> context = new DefaultContext<>();

            // 这里一定要先放chain，再放node，因为node优先于chain，所以当重名时，node会覆盖掉chain
            // 往上下文里放入所有的chain，是的el表达式可以直接引用到chain
            FlowBus.getChainMap().values().forEach(chain -> context.put(chain.getChainId(), chain));

            // 往上下文里放入所有的node，使得el表达式可以直接引用到nodeId
            FlowBus.getNodeMap().keySet().forEach(nodeId -> context.put(nodeId, FlowBus.getNode(nodeId)));

            if (nodes != null) {
                nodes.forEach(node -> {
                    JSONObject obj = JSONUtil.parseObj(node.toString());
                    String nodeId = obj.getStr("id");
                    String type = obj.getStr("type");
                    if (StrUtil.isNotBlank(nodeId) && StrUtil.isNotBlank(type)) {
                        Node nodeCustomer = new Node();
                        nodeCustomer.setType(NodeTypeEnum.getEnumByCode(type));
                        nodeCustomer.setId(nodeId);
                        nodeCustomer.setClazz(obj.getStr("clazz"));
                        context.put(nodeId, nodeCustomer);
                    }
                });
            }

            // 解析el成为一个Condition
            // 为什么这里只是一个Condition，而不是一个List<Condition>呢
            // 这里无论多复杂的，外面必定有一个最外层的Condition，所以这里只有一个，内部可以嵌套很多层，这点和以前的不太一样
            Condition condition = (Condition) EXPRESS_RUNNER.execute(elStr, context, errorList, true, true);

            if (Objects.isNull(condition)) {
                throw new QLException(StrUtil.format("parse el fail,el:[{}]", elStr));
            }
            return condition;
        } catch (QLException e) {
            // EL 底层会包装异常，这里是曲线处理
            if (ObjectUtil.isNotNull(e.getCause()) && Objects.equals(e.getCause().getMessage(), DataNotFoundException.MSG)) {
                // 构建错误信息
                String msg = buildDataNotFoundExceptionMsg(elStr);
                throw new ELParseException(msg);
            } else if (ObjectUtil.isNotNull(e.getCause())) {
                throw new ELParseException(e.getCause().getMessage());
            } else {
                throw new ELParseException(e.getMessage());
            }
        } catch (Exception e) {
            String errMsg = StrUtil.format("parse el fail;");
            throw new ELParseException(errMsg + e.getMessage());
        }
    }

    /**
     * 解析 EL 表达式，查找未定义的 id 并构建错误信息
     *
     * @param elStr el 表达式
     */
    private static String buildDataNotFoundExceptionMsg(String elStr) {
        String msg = String.format("[node/chain is not exist or node/chain not register]\n EL: %s",
                StrUtil.trim(elStr));
        try {
            InstructionSet parseResult = EXPRESS_RUNNER.getInstructionSetFromLocalCache(elStr);
            if (parseResult == null) {
                return msg;
            }

            String[] outAttrNames = parseResult.getOutAttrNames();
            if (ArrayUtil.isEmpty(outAttrNames)) {
                return msg;
            }

            List<String> chainIds = CollUtil.map(FlowBus.getChainMap().values(), Chain::getChainId, true);
            List<String> nodeIds = CollUtil.map(FlowBus.getNodeMap().values(), Node::getId, true);
            for (String attrName : outAttrNames) {
                if (!chainIds.contains(attrName) && !nodeIds.contains(attrName)) {
                    msg = String.format(
                            "[%s] is not exist or [%s] is not registered, you need to define a node or chain with id [%s] and register it \n EL: ",
                            attrName, attrName, attrName);

                    // 去除 EL 表达式中的空格和换行符
                    String sourceEl = StrUtil.removeAll(elStr, CharUtil.SPACE, CharUtil.LF, CharUtil.CR);
                    // 这里需要注意的是，nodeId 和 chainId 可能是关键字的一部分，如果直接 indexOf(attrName) 会出现误判
                    // 所以需要判断 attrName 前后是否有 ","
                    int commaRightIndex = sourceEl.indexOf(attrName + StrUtil.COMMA);
                    if (commaRightIndex != -1) {
                        // 需要加上 "EL: " 的长度 4，再加上 "^" 的长度 1，indexOf 从 0 开始，所以还需要加 1
                        return msg + sourceEl + "\n" + StrUtil.fill("^", CharUtil.SPACE, commaRightIndex + 6, true);
                    }
                    int commaLeftIndex = sourceEl.indexOf(StrUtil.COMMA + attrName);
                    if (commaLeftIndex != -1) {
                        // 需要加上 "EL: " 的长度 4，再加上 "^" 的长度 1，再加上 "," 的长度 1，indexOf 从 0
                        // 开始，所以还需要加 1
                        return msg + sourceEl + "\n" + StrUtil.fill("^", CharUtil.SPACE, commaLeftIndex + 7, true);
                    }
                    // 还有一种特殊情况，就是 EL 表达式中的节点使用 node("a")
                    int nodeIndex = sourceEl.indexOf(String.format("node(\"%s\")", attrName));
                    if (nodeIndex != -1) {
                        // 需要加上 "EL: " 的长度 4，再加上 “node("” 长度 6，再加上 "^" 的长度 1，indexOf 从 0
                        // 开始，所以还需要加 1
                        return msg + sourceEl + "\n" + StrUtil.fill("^", CharUtil.SPACE, commaLeftIndex + 12, true);
                    }
                }
            }
        } catch (Exception ex) {
            // ignore
        }
        return msg;
    }
}
