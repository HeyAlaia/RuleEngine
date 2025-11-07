import { doElJsonToGraph, doOtherElJsonToGraph } from "..";
import { ELCondition, NodePortName } from "../../node/type";
import { X6EdgePortJson, X6GraphJson } from "../type";



export const elJsonToGraph = (graphJson: X6GraphJson, json: ELCondition, source: X6EdgePortJson | undefined): { id: string } | undefined => {
    let edgeSource = source
    if (json.tag) {
        const res = doOtherElJsonToGraph(graphJson, json, edgeSource)
        if (res) {
            edgeSource = {
                cell: res.id,
                port: NodePortName.NEXT_PORT
            }
        }
    }

    const preELJsonList = json.executableGroup.PRE_KEY
    if (!!preELJsonList) {
        preELJsonList.forEach((item) => {
            const res = doElJsonToGraph(graphJson, item, edgeSource)
            if (res) {
                edgeSource = {
                    cell: res.id,
                    port: NodePortName.NEXT_PORT
                }
            }
        })
    }

    const defaultELJsonList = json.executableGroup.DEFAULT_KEY
    if (!!defaultELJsonList) {
        defaultELJsonList.forEach((item) => {
            const res = doElJsonToGraph(graphJson, item, edgeSource)
            if (res) {
                edgeSource = {
                    cell: res.id,
                    port: NodePortName.NEXT_PORT
                }
            }
        })
    }


    const finallyELJsonList = json.executableGroup.FINALLY_KEY
    if (!!finallyELJsonList) {
        finallyELJsonList.forEach((item) => {
            const res = doElJsonToGraph(graphJson, item, edgeSource)
            if (res) {
                edgeSource = {
                    cell: res.id,
                    port: NodePortName.NEXT_PORT
                }
            }
        })
    }


    return
}