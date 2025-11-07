import type { Graph, Node } from '@antv/x6';
import type { VueShapeConfig } from '@antv/x6-vue-shape';

import type { ELCondition, ELJson, NodeData, NodeLFData } from '../type';

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
  unsealAndWrapper,
  unsealThenWrapper,
} from '../common';
import nodeVue from '../node.vue';
import { ConditionType, ExecuteType, NodePortName } from '../type';

const TRUE_PORT = 'truePort';
const FALSE_PORT = 'falsePort';
const BOOL_PORT = 'elPort';

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
          name: BOOL_PORT,
          id: BOOL_PORT,
          attrs: {
            text: {
              text: 'bool',
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
          name: FALSE_PORT,
          id: FALSE_PORT,
          attrs: {
            text: {
              text: 'false',
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
        {
          group: 'bottom',
          name: TRUE_PORT,
          id: TRUE_PORT,
          attrs: {
            text: {
              text: 'true',
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
      color: '#9C33FF',
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

  if (!childrenEdges || childrenEdges.length === 0) {
    return undefined;
  }

  // 获取当前节点的所有port分组 key为name value为portId
  const portIds = getAllPortId(node);
  // 获取当前节点的所有子节点，key为portId value为nodes
  const childrenNodes = getPortChildrenNodes(childrenEdges);

  // catch 返回
  const ifGroup: { [key: string]: ELJson[] } = {};
  const ifJson: ELCondition = {
    conditionType: ConditionType.if,
    executeType: ExecuteType.CONDITION,
    executableGroup: ifGroup,
  };

  // 设置tag
  const tagPortId = portIds[NodePortName.TAG_PORT] ?? '';
  // 获取后继节点的所有子节点
  const tagnodes = childrenNodes[tagPortId];
  const tag = tagNodeStr(tagnodes);
  if (tag) {
    ifJson.tag = tag;
  }
  // catch port 端点
  const ifKeyGroupList = [] as ELJson[];
  ifGroup.IF_KEY = ifKeyGroupList;
  // 获取后继节点的portId
  const boolPortId = portIds[BOOL_PORT] ?? '';

  // 获取后继节点的所有子节点
  const boolNodes = childrenNodes[boolPortId];
  if (!boolNodes) {
    return undefined;
  }
  ifKeyGroupList.push(unsealAndWrapper(andOrWrapperV2(graph, boolNodes)));

  // true port 端点
  const truePortId = portIds[TRUE_PORT] ?? '';
  const trueNodes = childrenNodes[truePortId];
  if (trueNodes) {
    const trueKey = thenWrapper(graph, trueNodes[0]);
    if (trueKey) {
      ifGroup.IF_TRUE_CASE_KEY = [unsealThenWrapper(trueKey)];
    }
  }

  // false port 端点
  const falsePortId = portIds[FALSE_PORT] ?? '';
  const falseNodes = childrenNodes[falsePortId];
  if (falseNodes) {
    const falseKey = thenWrapper(graph, falseNodes[0]);
    if (falseKey) {
      ifGroup.IF_FALSE_CASE_KEY = [unsealThenWrapper(falseKey)];
    }
  }

  // 获取后继节点的portId
  const nextPortId = portIds[NodePortName.NEXT_PORT] ?? '';

  // 获取后继节点的所有子节点
  const nextNodes = childrenNodes[nextPortId];
  return nextWrapper(graph, ifJson, nextNodes);
};

// const buildJsonToNode = (graph: Graph, node: Node) => {};

// 么有父节点时候，是rootNode
const isRootNode = (graph: Graph, node: Node) => {
  const childNode = getParentNodes(graph, node);
  return !childNode || childNode.length === 0;
};
