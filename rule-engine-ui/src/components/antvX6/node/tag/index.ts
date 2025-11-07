import type { Graph, Node } from '@antv/x6';
import type { VueShapeConfig } from '@antv/x6-vue-shape';

import type { ELJson, NodeData, NodeLFData } from '../type';

import {
  getAllPortId,
  getChildrenEdges,
  getParentNodes,
  getPortChildrenNodes,
  initNodeX6Data,
  ports,
  thenWrapper,
} from '../common';
import nodeVue from './tag.vue';
import { NodePortName } from '../type';

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
        {
          group: 'left',
          name: NodePortName.BEFORE_PORT,
          id: NodePortName.BEFORE_PORT,
        },
        {
          group: 'right',
          name: NodePortName.NEXT_PORT,
          id: NodePortName.NEXT_PORT,
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
      color: '#33CCFF',
      width: 200,
      height: 60,
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
  const data = node.getData() as NodeData;

  // 获取当前节点的所有port分组 key为name value为portId
  const portIds = getAllPortId(node);
  // 获取当前节点的所有子节点，key为portId value为nodes
  const childrenNodes = getPortChildrenNodes(childrenEdges);

  // 获取后继节点的portId
  const nextPortId = portIds[NodePortName.NEXT_PORT] ?? '';

  // 获取后继节点的所有子节点
  const nextNodes = childrenNodes[nextPortId];
  if (!nextNodes) {
    return undefined;
  }
  const elJson = thenWrapper(graph, nextNodes[0]);
  if (elJson) {
    elJson.tag = data.liteInfo.cmpData?.tag;
  }
  return elJson;
};

// const buildJsonToNode = (graph: Graph, node: Node) => {};

// 么有父节点时候，是rootNode
const isRootNode = (graph: Graph, node: Node) => {
  const childNode = getParentNodes(graph, node);
  return !childNode || childNode.length === 0;
};
