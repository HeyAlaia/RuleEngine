import type { Graph, Node } from '@antv/x6';
import type { VueShapeConfig } from '@antv/x6-vue-shape';

import type {
  ELCondition,
  ELJson,
  ELNode,
  NodeData,
  NodeLFData,
} from '../type';

import {
  andOrWrapperV2,
  getAllPortId,
  getChildrenEdges,
  getParentNodes,
  getPortChildrenNodes,
  initNodeX6Data,
  nextWrapper,
  ports,
  tagNodeStr,
  thenWrapper,
  unsealThenWrapper,
} from '../common';
import nodeVue from '../node.vue';
import { ConditionType, ExecuteType, NodePortName, NodeShapes } from '../type';

const DO_PORT = 'doPort';
const BREAK_PORT = 'breakPort';

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
          group: 'top',
          name: BREAK_PORT,
          id: BREAK_PORT,
          attrs: {
            text: {
              text: 'break',
              style: {
                fontSize: '12px',
                visibility: 'hidden',
                display: 'none',
              },
            },
          },
          label: {
            position: 'top',
          },
        },
        {
          group: 'bottom',
          name: DO_PORT,
          id: DO_PORT,
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
      color: '#33FFF6',
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

  if (!childrenEdges || childrenEdges.length === 0) {
    return undefined;
  }

  // 获取当前节点的所有port分组 key为name value为portId
  const portIds = getAllPortId(node);
  // 获取当前节点的所有子节点，key为portId value为nodes
  const childrenNodes = getPortChildrenNodes(childrenEdges);

  // iterator 返回
  const iteratorGroup: { [key: string]: ELJson[] } = {};
  const iteratorJson: ELCondition = {
    conditionType: ConditionType.iterator,
    executeType: ExecuteType.CONDITION,
    executableGroup: iteratorGroup,
  };

  // 设置tag
  const tagPortId = portIds[NodePortName.TAG_PORT] ?? '';
  // 获取后继节点的所有子节点
  const tagnodes = childrenNodes[tagPortId];
  const tag = tagNodeStr(tagnodes);
  if (tag) {
    iteratorJson.tag = tag;
  }

  // for port 端点
  const iteratorKeyGroupList = [] as ELJson[];
  iteratorGroup.ITERATOR_KEY = iteratorKeyGroupList;
  const data = node.getData() as NodeData;
  const iteratorKeyJson: ELNode = {
    id: data.liteInfo.id,
    type: NodeShapes.ITERATOR,
    executeType: ExecuteType.NODE,
    cmpData: data.liteInfo.cmpData,
  };

  iteratorKeyGroupList.push(iteratorKeyJson);

  // do port 端点
  const doPortId = portIds[DO_PORT] ?? '';
  const doNodes = childrenNodes[doPortId];
  if (doNodes) {
    const doKey = thenWrapper(graph, doNodes[0]);
    if (doKey) {
      iteratorGroup.DO_KEY = [unsealThenWrapper(doKey)];
    }
  }

  // break port 端点
  const breakKeyGroupList = [] as ELJson[];
  const breakPortId = portIds[BREAK_PORT] ?? '';
  const breakNodes = childrenNodes[breakPortId];
  if (breakNodes) {
    breakKeyGroupList.push(andOrWrapperV2(graph, breakNodes));
    iteratorGroup.BREAK_KEY = breakKeyGroupList;
  }

  // 获取后继节点的portId
  const nextPortId = portIds[NodePortName.NEXT_PORT] ?? '';

  // 获取后继节点的所有子节点
  const nextNodes = childrenNodes[nextPortId];
  return nextWrapper(graph, iteratorJson, nextNodes);
};

// const buildJsonToNode = (graph: Graph, node: Node) => {};

// 么有父节点时候，是rootNode
const isRootNode = (graph: Graph, node: Node) => {
  const childNode = getParentNodes(graph, node);
  return !childNode || childNode.length === 0;
};
