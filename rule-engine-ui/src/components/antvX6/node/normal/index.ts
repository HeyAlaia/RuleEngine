import type { Graph, Node } from '@antv/x6';
import type { VueShapeConfig } from '@antv/x6-vue-shape';

import type { ELJson, ELNode, NodeData, NodeLFData } from '../type';
import { ExecuteType, NodePortName, NodeShapes } from '../type';

import { getAllPortId, getChildrenEdges, getParentNodes, getPortChildrenNodes, initNodeX6Data, nextWrapper, ports, tagNodeStr } from '../common';
import nodeVue from '../node.vue';

export const shapeConfig = (liteData: NodeLFData) => {
	return {
		shape: liteData.id,
		size: {
			width: 160,
			height: 30,
		},
		component: nodeVue,
		label: liteData.label,
		ports: {
			...ports,
			items: [...ports.items],
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
      color: '#3357FF',
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
  const data = node.getData() as NodeData;
  const nodeJson: ELNode = {
    id: data.liteInfo.id,
    type: NodeShapes.COMMON,
    executeType: ExecuteType.NODE,
    cmpData: data.liteInfo.cmpData,
  };

  // 没有后续节点，返回当前节点
  if (!childrenEdges || childrenEdges.length === 0) {
    return nodeJson;
  }

  // 获取当前节点的所有port分组 key为name value为portId
  const portIds = getAllPortId(node);
  // 获取当前节点的所有子节点，key为portId value为nodes
  const childrenNodes = getPortChildrenNodes(childrenEdges);

  // 设置tag
  const tagPortId = portIds[NodePortName.TAG_PORT] ?? '';
  // 获取后继节点的所有子节点
  const tagnodes = childrenNodes[tagPortId];
  const tag = tagNodeStr(tagnodes);
  if (tag) {
    nodeJson.tag = tag;
  }

  // 获取后继节点的portId
  const nextPortId = portIds[NodePortName.NEXT_PORT] ?? '';

  // 获取后继节点的所有子节点
  const nextNodes = childrenNodes[nextPortId];
  return nextWrapper(graph, nodeJson, nextNodes);
};

// const buildJsonToNode = (graph: Graph, node: Node) => {};

// 么有父节点时候，是rootNode
const isRootNode = (graph: Graph, node: Node) => {
  const childNode = getParentNodes(graph, node);
  return !childNode || childNode.length === 0;
};
