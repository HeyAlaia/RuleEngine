import { doElJsonToGraph, doOtherElJsonToGraph } from "..";
import { ELCondition, NodePortName } from "../../node/type"
import { X6EdgePortJson, X6GraphJson } from "../type"
import { v4 as uuidv4 } from 'uuid';

// 获取连接桩名
const CATCH_PORT = 'catchPort';

// 获取连接桩名
const DO_PORT = 'doPort';


export const elJsonToGraph = (graphJson: X6GraphJson, json: ELCondition, source: X6EdgePortJson | undefined): { id: string } | undefined => {
    let edgeSource = source
    const nodeId = uuidv4()
    const edgeId = uuidv4()
    graphJson.nodes.push({
        id: nodeId,
        shape: "pippin-catch"
    })
    if (edgeSource) {
        graphJson.edges.push({
            id: edgeId,
            shape: "edge",
            source: edgeSource,
            target: { cell: nodeId, port: NodePortName.BEFORE_PORT },
        })
    }

    if (json.tag) {
        let edgeSource = {
            cell: nodeId,
            port: NodePortName.TAG_PORT
        }
        doOtherElJsonToGraph(graphJson, json, edgeSource)
    }

    const doELJsonList = json.executableGroup.DO_KEY
    if (!!doELJsonList) {
        let edgeSource = {
            cell: nodeId,
            port: DO_PORT
        }
        doELJsonList.forEach((item) => {
            doElJsonToGraph(graphJson, item, edgeSource)
        })
    }
    const catchELJsonList = json.executableGroup.CATCH_KEY
    if (!!catchELJsonList) {
        let edgeSource = {
            cell: nodeId,
            port: CATCH_PORT
        }
        catchELJsonList.forEach((item) => {
            doElJsonToGraph(graphJson, item, edgeSource)
        })
    }
    return {
        id: nodeId
    }
}