import { isElCondition, isElNode } from "../node/common";
import { ELJson, ExecuteType, NodePortName } from "../node/type";
import { elX6ConditionGraph, elX6Graph, elX6NodeGraph } from "./factory";
import { X6EdgePortJson, X6GraphJson } from "./type";


export const doElJsonToGraph = (graphJson: X6GraphJson, json: ELJson, source: X6EdgePortJson | undefined): { id: string } | undefined => {
    let res
    if (json.executeType == ExecuteType.CONDITION && isElCondition(json)) {
        res = elX6ConditionGraph.get(json.conditionType)?.(graphJson, json, source)
    } else if (json.executeType == ExecuteType.NODE && isElNode(json)) {
        res = elX6NodeGraph.get(json.type)?.(graphJson, json, source)
        if (json.tag && res?.id) {
            let source = {
                cell: res.id,
                port: NodePortName.TAG_PORT
            }
            doOtherElJsonToGraph(graphJson, json, source)
        }
    }
    return res
}

export const doOtherElJsonToGraph = (graphJson: X6GraphJson, json: ELJson, source: X6EdgePortJson | undefined): { id: string } | undefined => {
    if (json.tag) {
        return elX6Graph.get("tag")?.(graphJson, json, source)
    }
}

