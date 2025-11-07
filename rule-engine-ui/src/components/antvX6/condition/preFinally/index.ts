import { doElJsonToGraph, doOtherElJsonToGraph } from "..";
import { ELCondition, NodePortName } from "../../node/type"
import { X6EdgePortJson, X6GraphJson } from "../type"
import { v4 as uuidv4 } from 'uuid';


export const elJsonPreToGraphCondition = (graphJson: X6GraphJson, json: ELCondition, source: X6EdgePortJson | undefined): { id: string } | undefined => {
    let edgeSource = source
    const nodeId = uuidv4()
    const edgeId = uuidv4()
    graphJson.nodes.push({
        id: nodeId,
        shape: "pippin-pre"
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

    const defaultELJsonList = json.executableGroup.DEFAULT_KEY
    if (!!defaultELJsonList.length) {
        let edgeSource = {
            cell: nodeId,
            port: "defaultPort"
        }
        defaultELJsonList.forEach((item) => {
            const res = doElJsonToGraph(graphJson, item, edgeSource)
            if (res) {
                edgeSource = {
                    cell: res.id,
                    port: "defaultPort"
                }
            }
        })
    }
    return {
        id: nodeId
    }
}


export const elJsonFinallyToGraphCondition = (graphJson: X6GraphJson, json: ELCondition, source: X6EdgePortJson | undefined): { id: string } | undefined => {
    let edgeSource = source
    const nodeId = uuidv4()
    const edgeId = uuidv4()
    graphJson.nodes.push({
        id: nodeId,
        shape: "pippin-finally"
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

    const defaultELJsonList = json.executableGroup.DEFAULT_KEY
    if (!!defaultELJsonList.length) {
        let edgeSource = {
            cell: nodeId,
            port: "defaultPort"
        }
        defaultELJsonList.forEach((item) => {
            const res = doElJsonToGraph(graphJson, item, edgeSource)
            if (res) {
                edgeSource = {
                    cell: res.id,
                    port: "defaultPort"
                }
            }
        })
    }
    return {
        id: nodeId
    }
}