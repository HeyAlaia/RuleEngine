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
  isElCondition,
  ports,
  tagNodeStr,
} from '../common';
import nodeVue from '../node.vue';
import { ConditionType, ExecuteType, NodePortName, NodeShapes } from '../type';

export const shapeConfig = (liteData: NodeLFData) => {
  return {
    shape: liteData.id,
    width: 160,
    height: 30,
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
      color: '#FF5733',
    },
    liteInfo: liteData,
  } as NodeData;
};

/**
 * 构建节点数据
 * 通过json进行互转，如果直接构建el，不方便回显
 */
const buildNodeToJson = (graph: Graph, node: Node): ELJson => {
  const childrenEdges = getChildrenEdges(graph, node);
  const data = node.getData() as NodeData;
  const nodeJson: ELNode = {
    id: data.liteInfo.id,
    type: NodeShapes.BOOLEAN,
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
  const nodes = childrenNodes[nextPortId];

  // 校验是否有后继节点
  if (!nodes || nodes.length === 0) {
    return nodeJson;
  }

  const execGroup: { [key: string]: ELJson[] } = {};
  const execGroupList = [] as ELJson[];
  const conditionJson: ELCondition = {
    booleanConditionType: 'AND',
    conditionType: ConditionType.and_or_opt,
    executeType: ExecuteType.CONDITION,
    executableGroup: execGroup,
  };
  execGroup.AND_OR_ITEM = execGroupList;
  execGroupList.push(
    nodeJson,
    ...nextPortBuild(graph, nodes).filter((item) => !!item),
  );
  return conditionJson;
};

const nextPortBuild = (graph: Graph, childrenNodes: Node[]): ELJson[] => {
  if (childrenNodes.length === 1 && childrenNodes[0]) {
    const data = childrenNodes[0].getData() as NodeData;
    const elJson = data.buildNodeToJson(graph, childrenNodes[0]);
    if (!elJson) {
      return [];
    }
    // 合并AND节点
    if (
      elJson &&
      isElCondition(elJson) &&
      elJson.booleanConditionType === 'AND'
    ) {
      const andItem = elJson.executableGroup.AND_OR_ITEM;
      if (andItem) {
        return andItem;
      }
    } else {
      // 非AND节点，叶子节点或者OR节点
      return [elJson];
    }
  }

  // 多个是并
  const execOrGroup: { [key: string]: ELJson[] } = {};
  const execOrGroupList = [] as ELJson[];
  execOrGroup.AND_OR_ITEM = execOrGroupList;
  const orJson: ELCondition = {
    booleanConditionType: 'OR',
    conditionType: ConditionType.and_or_opt,
    executeType: ExecuteType.CONDITION,
    executableGroup: execOrGroup,
  };

  childrenNodes.forEach((item: Node) => {
    const data = item.getData() as NodeData;
    const elJson = data.buildNodeToJson(graph, item);
    if (elJson) {
      execOrGroupList.push(elJson);
    }
  });

  return [orJson];
};

// const buildJsonToNode = (graph: Graph, node: Node) => {};

// 么有父节点时候，是rootNode
const isRootNode = (graph: Graph, node: Node) => {
  const childNode = getParentNodes(graph, node);
  return !childNode || childNode.length === 0;
};
