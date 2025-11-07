import { ELJson, NodePortName } from "../../node/type"
import { X6EdgePortJson, X6GraphJson } from "../type"
import { v4 as uuidv4 } from 'uuid';


export const elJsonToGraph = (graphJson: X6GraphJson, json: ELJson, source: X6EdgePortJson | undefined): { id: string } | undefined => {
    const nodeId = uuidv4()
    const edgeId = uuidv4()
    graphJson.nodes.push({
        id: nodeId,
        shape: "pippin-tag",
        cmpData: { "tag": json.tag },
    })
    if (source) {
        graphJson.edges.push({
            id: edgeId,
            shape: "edge",
            source: source,
            target: { cell: nodeId, port: NodePortName.BEFORE_PORT },
        })
    }
    return {
        id: nodeId
    }
}