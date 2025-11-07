import type { Graph, Node } from '@antv/x6';
import type { VueShapeConfig } from '@antv/x6-vue-shape';

import type { ELCondition, ELJson, NodeData, NodeLFData } from '../type';

import {
  getAllPortId,
  getChildrenEdges,
  getParentNodes,
  getPortChildrenNodes,
  initNodeX6Data,
  nextWrapper,
  ports,
  tagNodeStr,
  thenWrapper,
} from '../common';
import nodeVue from '../node.vue';
import { ExecuteType, NodePortName, NodeShapes } from '../type';

const DEFAULT_PORT = 'defaultPort';

export const shapeConfig = (liteData: NodeLFData) => {
  return {
    shape: liteData.id,
    width: 160,
    height: 30,
    component: nodeVue,
    label: liteData.label,
    ports: {
      ...ports,
      items: [...ports.items,
      {
        group: 'bottom',
        name: DEFAULT_PORT,
        id: DEFAULT_PORT,
        attrs: {
          text: {
            text: 'default',
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
      color: '#33FF9C',
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

  const finallyExecGroup: { [key: string]: ELJson[] } = {};
  const finallyCondition: ELCondition = {
    conditionType: NodeShapes.FINALLY,
    executeType: ExecuteType.CONDITION,
    executableGroup: finallyExecGroup,
  };

  // 获取当前节点的所有port分组 key为name value为portId
  const portIds = getAllPortId(node);
  // 获取当前节点的所有子节点，key为portId value为nodes
  const childrenNodes = getPortChildrenNodes(childrenEdges);

  // 设置tag
  const tagPortId = portIds[NodePortName.TAG_PORT] ?? '';
  // 获取后继节点的所有子节点
  const tagNodes = childrenNodes[tagPortId];
  const tag = tagNodeStr(tagNodes);
  if (tag) {
    finallyCondition.tag = tag;
  }

  const defaultPortId = portIds[DEFAULT_PORT] ?? '';
  const defaultNodes = childrenNodes[defaultPortId];

  const nextEl = thenWrapper(graph, defaultNodes[0]);
  if (nextEl) {
    finallyExecGroup.DEFAULT_KEY = [nextEl];
  }
  // 获取后继节点的portId
  const nextPortId = portIds[NodePortName.NEXT_PORT] ?? '';

  // 获取后继节点的所有子节点
  const nextNodes = childrenNodes[nextPortId];
  return nextWrapper(graph, finallyCondition, nextNodes);
};

// const buildJsonToNode = (graph: Graph, node: Node) => {};

// 么有父节点时候，是rootNode
const isRootNode = (graph: Graph, node: Node) => {
  const childNode = getParentNodes(graph, node);
  return !childNode || childNode.length === 0;
};
