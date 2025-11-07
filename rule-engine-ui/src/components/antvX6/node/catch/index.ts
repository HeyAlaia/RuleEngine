import type { Graph, Node } from '@antv/x6';
import type { VueShapeConfig } from '@antv/x6-vue-shape';

import type { ELCondition, ELJson, NodeData, NodeLFData } from '../type';

import {
  getAllPortId,
  getChildrenEdges,
  getParentNodes,
  getPortChildrenNodes,
  initNodeX6Data,
  ports,
  tagNodeStr,
  nextWrapper,
  thenWrapper,
  unsealThenWrapper,
} from '../common';
import nodeVue from '../node.vue';
import { ConditionType, ExecuteType, NodePortName } from '../type';

// 获取连接桩名
const CATCH_PORT = 'catchPort';

// 获取连接桩名
const DO_PORT = 'doPort';

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
            position: 'top',
          },
        },
        {
          group: 'bottom',
          name: CATCH_PORT,
          id: CATCH_PORT,
          attrs: {
            text: {
              text: 'catch',
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
      color: '#FF33A1',
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
  const catchGroup: { [key: string]: ELJson[] } = {};
  const catchJson: ELCondition = {
    conditionType: ConditionType.catch,
    executeType: ExecuteType.CONDITION,
    executableGroup: catchGroup,
  };

  // 设置tag
  const tagPortId = portIds[NodePortName.TAG_PORT] ?? '';
  // 获取后继节点的所有子节点
  const tagnodes = childrenNodes[tagPortId];
  const tag = tagNodeStr(tagnodes);
  if (tag) {
    catchJson.tag = tag;
  }

  // catch port 端点
  const catchPortId = portIds[CATCH_PORT] ?? '';
  const chtchNodes = childrenNodes[catchPortId];
  if (chtchNodes) {
    const catchKey = thenWrapper(graph, chtchNodes[0]);
    if (catchKey) {
      catchGroup.CATCH_KEY = [unsealThenWrapper(catchKey)];
    }
  }

  // do port 端点
  const doPortId = portIds[DO_PORT] ?? '';
  const doNodes = childrenNodes[doPortId];
  if (doNodes) {
    const doKey = thenWrapper(graph, doNodes[0]);
    if (doKey) {
      catchGroup.DO_KEY = [unsealThenWrapper(doKey)];
    }
  }

  // 获取后继节点的portId
  const nextPortId = portIds[NodePortName.NEXT_PORT] ?? '';
  // 获取后继节点的所有子节点
  const nextNodes = childrenNodes[nextPortId];
  return nextWrapper(graph, catchJson, nextNodes);
};

// const buildJsonToNode = (graph: Graph, node: Node) => {};

// 么有父节点时候，是rootNode
const isRootNode = (graph: Graph, node: Node) => {
  const childNode = getParentNodes(graph, node);
  return !childNode || childNode.length === 0;
};
