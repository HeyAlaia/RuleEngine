import type { Graph, Node } from '@antv/x6';
import type { VueShapeConfig } from '@antv/x6-vue-shape';

import type { ELJson, NodeData, NodeLFData } from '../type';
import { NodePortName } from '../type';

import { getAllPortId, getChildrenEdges, getParentNodes, getPortChildrenNodes, initNodeX6Data, nextWrapper, ports, whenWrapper } from '../common';
import nodeVue from '../node.vue';

const WHEN_PORT = 'whenPort';

export const shapeConfig = (liteData: NodeLFData) => {
    return {
        shape: liteData.id,
        width: 160,
        height: 30,
        component: nodeVue,
        label: liteData.label,
        ports: {
            ...ports,
            items: [
                ...ports.items,
                {
                    group: 'bottom',
                    name: WHEN_PORT,
                    id: WHEN_PORT,
                    attrs: {
                        text: {
                            text: 'do',
                            style: {
                                fontSize: '12px',
                                visibility: 'hidden',
                                display: 'none',
                            },
                        },
                    },
                    label: {
                        position: 'bottom',
                    },
                },
            ],
        },
        data: data(liteData),
    } as VueShapeConfig;
};

const data = (liteData: NodeLFData): NodeData => {
    return {
        buildNodeToJson,
        isRootNode,
        x6Info: {
            ...initNodeX6Data,
            color: '#FF9933',
            width: 160,
            height: 30,
        },
        liteInfo: liteData,
    } as NodeData;
};

/**
 * 构建节点数据
 * 通过json进行互转，如果直接构建el，不方便回显
 */
const buildNodeToJson = (graph: Graph, node: Node): ELJson | undefined => {
    const childrenEdges = getChildrenEdges(graph, node);

    // 没有后续节点，返回当前节点
    if (!childrenEdges || childrenEdges.length === 0) {
        return undefined;
    }

    // 获取当前节点的所有port分组 key为name value为portId
    const portIds = getAllPortId(node);
    // 获取当前节点的所有子节点，key为portId value为nodes
    const childrenNodes = getPortChildrenNodes(childrenEdges);

    // when port 端点
    const whenPortId = portIds[WHEN_PORT] ?? '';
    const whenNodes = childrenNodes[whenPortId];
    let whenCondtion
    if (whenNodes) {
        whenCondtion = whenWrapper(graph, whenNodes);
    }

    // 获取后继节点的portId
    const nextPortId = portIds[NodePortName.NEXT_PORT] ?? '';
    const nextNodes = childrenNodes[nextPortId];

    return nextWrapper(graph, whenCondtion, nextNodes);
};


// 么有父节点时候，是rootNode
const isRootNode = (graph: Graph, node: Node) => {
    const childNode = getParentNodes(graph, node);
    return !childNode || childNode.length === 0;
};
