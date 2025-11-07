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

const DEFAULT_PORT = 'defaultPort';
const SWITCH_PORT = 'switchPort';

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
            position: 'top',
          },
        },
        {
          group: 'bottom',
          name: SWITCH_PORT,
          id: SWITCH_PORT,
          attrs: {
            text: {
              text: 'switch',
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
      color: '#FFFF33',
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

  // switch 返回
  const switchGroup: { [key: string]: ELJson[] } = {};
  const switchJson: ELCondition = {
    conditionType: ConditionType.switch,
    executeType: ExecuteType.CONDITION,
    executableGroup: switchGroup,
  };

  // switch port 端点
  const switchKeyGroupList = [] as ELJson[];
  switchGroup.SWITCH_KEY = switchKeyGroupList;
  const data = node.getData() as NodeData;
  const switchKeyJson: ELNode = {
    id: data.liteInfo.id,
    type: NodeShapes.SWITCH,
    executeType: ExecuteType.NODE,
    cmpData: data.liteInfo.cmpData,
  };

  // 设置tag
  const tagPortId = portIds[NodePortName.TAG_PORT] ?? '';
  // 获取后继节点的所有子节点
  const tagnodes = childrenNodes[tagPortId];
  const tag = tagNodeStr(tagnodes);
  if (tag) {
    switchKeyJson.tag = tag;
  }

  switchKeyGroupList.push(switchKeyJson);

  // default port 端点
  const defaultPortId = portIds[DEFAULT_PORT] ?? '';
  const defaultNodes = childrenNodes[defaultPortId];
  if (defaultNodes) {
    const defaultKey = thenWrapper(graph, defaultNodes[0]);
    if (defaultKey) {
      switchGroup.SWITCH_DEFAULT_KEY = [unsealThenWrapper(defaultKey)];
    }
  }

  // switch target port 端点
  const targetKeyGroupList = [] as ELJson[];
  const targetPortId = portIds[SWITCH_PORT] ?? '';
  const targetNodes = childrenNodes[targetPortId];
  if (!targetNodes) {
    return undefined;
  }

  const targetEls = targetNodes
    .map((tNode) => {
      const tNodeData = tNode.getData() as NodeData;
      const buildJson = tNodeData.buildNodeToJson(graph, tNode)
      if (buildJson) {
        return unsealThenWrapper(buildJson);
      }
    })
    .filter((tNode) => tNode !== undefined);
  targetKeyGroupList.push(...targetEls);
  switchGroup.SWITCH_TARGET_KEY = targetKeyGroupList;

  // 获取后继节点的portId
  const nextPortId = portIds[NodePortName.NEXT_PORT] ?? '';

  // 获取后继节点的所有子节点
  const nextNodes = childrenNodes[nextPortId];
  return nextWrapper(graph, switchJson, nextNodes);
};

// const buildJsonToNode = (graph: Graph, node: Node) => {};

// 么有父节点时候，是rootNode
const isRootNode = (graph: Graph, node: Node) => {
  const childNode = getParentNodes(graph, node);
  return !childNode || childNode.length === 0;
};
