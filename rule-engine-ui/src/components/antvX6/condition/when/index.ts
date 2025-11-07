import { doElJsonToGraph, doOtherElJsonToGraph } from "..";
import { ELCondition, NodePortName } from "../../node/type";
import { X6EdgePortJson, X6GraphJson } from "../type";
import { v4 as uuidv4 } from 'uuid';




const WHEN_PORT = 'whenPort';

export const elJsonToGraph = (graphJson: X6GraphJson, json: ELCondition, source: X6EdgePortJson | undefined): { id: string } | undefined => {
    const nodeId = uuidv4()
    const edgeId = uuidv4()
    let edgeSource = source
    graphJson.nodes.push({
        id: nodeId,
        shape: 'pippin-when',
    })



    if (edgeSource) {
        graphJson.edges.push({
            id: edgeId,
            shape: 'edge',
            source: edgeSource,
            target: {
                cell: nodeId,
                port: NodePortName.BEFORE_PORT
            }
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
        edgeSource = {
            cell: nodeId,
            port: WHEN_PORT
        }
        defaultELJsonList.forEach((item) => {
            doElJsonToGraph(graphJson, item, edgeSource)
        })
    }
    return {
        id: nodeId
    }
}