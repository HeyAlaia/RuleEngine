import { doElJsonToGraph, doOtherElJsonToGraph } from "..";
import { ELCondition, NodePortName } from "../../node/type"
import { X6EdgePortJson, X6GraphJson } from "../type"
import { v4 as uuidv4 } from 'uuid';


const TRUE_PORT = 'truePort';
const FALSE_PORT = 'falsePort';
const BOOL_PORT = 'elPort';

export const elJsonToGraph = (graphJson: X6GraphJson, json: ELCondition, source: X6EdgePortJson | undefined): { id: string } | undefined => {
    const nodeId = uuidv4()
    const edgeId = uuidv4()
    let edgeSource = source
    graphJson.nodes.push({
        id: nodeId,
        shape: 'pippin-if',
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

    const ifELJsonList = json.executableGroup.IF_KEY
    if (!!ifELJsonList) {
        let edgeSource = {
            cell: nodeId,
            port: BOOL_PORT
        }
        ifELJsonList.forEach((item) => {
            doElJsonToGraph(graphJson, item, edgeSource)
        })
    }
    const trueELJsonList = json.executableGroup.IF_TRUE_CASE_KEY
    if (!!trueELJsonList) {
        let edgeSource = {
            cell: nodeId,
            port: TRUE_PORT
        }
        trueELJsonList.forEach((item) => {
            doElJsonToGraph(graphJson, item, edgeSource)
        })
    }
    const falseELJsonList = json.executableGroup.IF_FALSE_CASE_KEY
    if (!!falseELJsonList) {
        let edgeSource = {
            cell: nodeId,
            port: FALSE_PORT
        }
        falseELJsonList.forEach((item) => {
            doElJsonToGraph(graphJson, item, edgeSource)
        })
    }
    return {
        id: nodeId
    }
}