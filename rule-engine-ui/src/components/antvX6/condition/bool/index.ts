import { doElJsonToGraph } from "..";
import { isElNode } from "../../node/common";
import { ELCondition, ELNode, NodePortName } from "../../node/type"
import { X6EdgePortJson, X6GraphJson } from "../type"
import { v4 as uuidv4 } from 'uuid';


export const elJsonToGraph = (graphJson: X6GraphJson, json: ELNode, source: X6EdgePortJson | undefined): { id: string } | undefined => {
    const nodeId = uuidv4()
    const edgeId = uuidv4()
    if (isElNode(json)) {
        graphJson.nodes.push({
            id: nodeId,
            shape: json.id,
            cmpData: json.cmpData,
        })
        if (source) {
            graphJson.edges.push({
                id: edgeId,
                shape: "edge",
                source: source,
                target: { cell: nodeId, port: NodePortName.BEFORE_PORT },
            })
        }
    }
    return {
        id: nodeId
    }
}

export const elNotJsonToGraphCondition = (graphJson: X6GraphJson, json: ELCondition, source: X6EdgePortJson | undefined): { id: string } | undefined => {
    const nodeId = uuidv4()
    const edgeId = uuidv4()
    let edgeSource = source
    if (!edgeSource) {
        return
    }
    graphJson.nodes.push({
        id: nodeId,
        shape: "pippin-not-bool",
    })
    graphJson.edges.push({
        id: edgeId,
        shape: "edge",
        source: edgeSource,
        target: { cell: nodeId, port: NodePortName.BEFORE_PORT },
    })
    edgeSource = {
        cell: nodeId,
        port: NodePortName.NEXT_PORT
    }
    const notELJsonList = json.executableGroup.NOT_ITEM_KEY
    notELJsonList.forEach((item) => {
        const res = doElJsonToGraph(graphJson, item, edgeSource)
        if (res) {
            edgeSource = {
                cell: res.id,
                port: NodePortName.NEXT_PORT
            }
        }
    })
    return
}

export const elJsonToGraphCondition = (graphJson: X6GraphJson, json: ELCondition, source: X6EdgePortJson | undefined): { id: string } | undefined => {
    let edgeSource = source
    if (json.booleanConditionType === 'AND') {
        const defaultELJsonList = json.executableGroup.AND_OR_ITEM
        defaultELJsonList.forEach((item) => {
            const res = doElJsonToGraph(graphJson, item, edgeSource)
            if (res) {
                edgeSource = {
                    cell: res.id,
                    port: NodePortName.NEXT_PORT
                }
            }
        })
    } else if (json.booleanConditionType === 'OR') {
        const defaultELJsonList = json.executableGroup.AND_OR_ITEM
        defaultELJsonList.forEach((item) => {
            doElJsonToGraph(graphJson, item, edgeSource)
        })
    }
    return
}