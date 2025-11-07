import { doElJsonToGraph, doOtherElJsonToGraph } from "..";
import { isElNode } from "../../node/common";
import { ELCondition, ELNode, NodePortName } from "../../node/type"
import { X6EdgePortJson, X6GraphJson } from "../type"
import { v4 as uuidv4 } from 'uuid';


const DEFAULT_PORT = 'defaultPort';
const SWITCH_PORT = 'switchPort';


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


export const elJsonToGraphCondition = (graphJson: X6GraphJson, json: ELCondition, source: X6EdgePortJson | undefined): { id: string } | undefined => {
    let edgeSource = source
    let switchId
    const swtchELJsonList = json.executableGroup.SWITCH_KEY
    if (!!swtchELJsonList) {
        swtchELJsonList.forEach((item) => {
            const res = doElJsonToGraph(graphJson, item, edgeSource)
            switchId = res?.id
        })
    }

    if (!switchId) {
        return
    }

    if (json.tag) {
        let edgeSource = {
            cell: switchId,
            port: NodePortName.TAG_PORT
        }
        doOtherElJsonToGraph(graphJson, json, edgeSource)
    }

    const targetELJsonList = json.executableGroup.SWITCH_TARGET_KEY
    if (!!targetELJsonList) {
        let edgeSource = {
            cell: switchId,
            port: SWITCH_PORT
        }
        targetELJsonList.forEach((item) => {
            doElJsonToGraph(graphJson, item, edgeSource)
        })
    }
    const deafaultELJsonList = json.executableGroup.SWITCH_DEFAULT_KEY
    if (!!deafaultELJsonList) {
        let edgeSource = {
            cell: switchId,
            port: DEFAULT_PORT
        }
        deafaultELJsonList.forEach((item) => {
            doElJsonToGraph(graphJson, item, edgeSource)
        })
    }
    return {
        id: switchId
    }
}