import { doElJsonToGraph, doOtherElJsonToGraph } from "..";
import { isElNode } from "../../node/common";
import { ELCondition, ELNode, NodePortName } from "../../node/type"
import { X6EdgePortJson, X6GraphJson } from "../type"
import { v4 as uuidv4 } from 'uuid';


const DO_PORT = 'doPort';
const BREAK_PORT = 'breakPort';


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
    let iteratorId
    const iteratorELJsonList = json.executableGroup.ITERATOR_KEY
    if (!!iteratorELJsonList) {
        iteratorELJsonList.forEach((item) => {
            const res = doElJsonToGraph(graphJson, item, edgeSource)
            iteratorId = res?.id
        })
    }

    if (!iteratorId) {
        return
    }

    if (json.tag) {
        let edgeSource = {
            cell: iteratorId,
            port: NodePortName.TAG_PORT
        }
        doOtherElJsonToGraph(graphJson, json, edgeSource)
    }

    const doELJsonList = json.executableGroup.DO_KEY
    if (!!doELJsonList) {
        let edgeSource = {
            cell: iteratorId,
            port: DO_PORT
        }
        doELJsonList.forEach((item) => {
            doElJsonToGraph(graphJson, item, edgeSource)
        })
    }
    const breakELJsonList = json.executableGroup.BREAK_KEY
    if (!!breakELJsonList) {
        let edgeSource = {
            cell: iteratorId,
            port: BREAK_PORT
        }
        breakELJsonList.forEach((item) => {
            doElJsonToGraph(graphJson, item, edgeSource)
        })
    }
    return {
        id: iteratorId
    }
}