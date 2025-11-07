import type { Cell, Edge, Graph, Node } from '@antv/x6';

import type { ELCondition, ELJson, ELNode, NodeData, NodeX6Data } from './type';

import { ConditionType, ExecuteType, NodePortName } from './type';
import { id } from 'element-plus/es/locale';

export const ports = {
  groups: {
    top: {
      position: 'top',
      attrs: {
        circle: {
          r: 4,
          magnet: true,
          stroke: '#5F95FF',
          strokeWidth: 1,
          fill: '#fff',
          style: {
            visibility: 'hidden',
          },
        },
      },
    },
    right: {
      position: 'right',
      attrs: {
        circle: {
          r: 4,
          magnet: true,
          stroke: '#5F95FF',
          strokeWidth: 1,
          fill: '#fff',
          style: {
            visibility: 'hidden',
          },
        },
      },
    },
    bottom: {
      position: 'bottom',
      attrs: {
        circle: {
          r: 4,
          magnet: true,
          stroke: '#5F95FF',
          strokeWidth: 1,
          fill: '#fff',
          style: {
            visibility: 'hidden',
          },
        },
      },
    },
    left: {
      position: 'left',
      attrs: {
        circle: {
          r: 4,
          magnet: true,
          stroke: '#5F95FF',
          strokeWidth: 1,
          fill: '#fff',
          style: {
            visibility: 'hidden',
          },
        },
      },
    },
  },
  items: [
    {
      group: 'top',
      name: NodePortName.TAG_PORT,
      id: NodePortName.TAG_PORT,
      attrs: {
        text: {
          text: 'tag',
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
};

export const initNodeX6Data: NodeX6Data = {
  selected: false,
  activated: false,
  width: 300,
  height: 100,
  color: '#fff',
};

export const getChildrenEdges = (
  graph: Graph,
  cell: Cell,
): Edge<Edge.Properties>[] | null => {
  return graph.getOutgoingEdges(cell);
};

export const getChildrenNodes = (
  graph: Graph,
  cell: Cell,
): Node<Node.Properties>[] | null => {
  const neighbors = graph.getNeighbors(cell, {
    outgoing: true,
  }) as Node<Node.Properties>[] | null;
  return neighbors;
};

export const getParentNodes = (
  graph: Graph,
  cell: Cell,
): Node<Node.Properties>[] | null => {
  const neighbors = graph.getNeighbors(cell, {
    incoming: true,
  }) as Node<Node.Properties>[] | null;
  return neighbors;
};

export const getParentEdges = (
  graph: Graph,
  cell: Cell,
): Edge<Edge.Properties>[] | null => {
  return graph.getIncomingEdges(cell);
};

// 导出一个名为 isElNode 的常量，它是一个函数
export const isElNode = (el: ELJson): el is ELNode => {
  return el.executeType === ExecuteType.NODE;
};

export const isElCondition = (el: ELJson): el is ELCondition => {
  return el.executeType === ExecuteType.CONDITION;
};

export const setError = (node: Cell | Node, message: string) => {
  node.setData({ x6Info: { error: message } });
};

export const setActivated = (node: Cell | Node, activated: boolean) => {
  node.setData({ x6Info: { activated } });
};

export const setCmpData = (
  node: Cell | Node,
  cmpData: { [key: string]: any },
) => {
  node.setData({ liteInfo: { cmpData } });
};

export const setSelected = (node: Cell | Node, selected: boolean) => {
  node.setData({ x6Info: { selected } });
};

// 分组获取不同port下的子节点
export const getPortChildrenNodes = (
  edge: Edge<Edge.Properties>[],
): { [key: string]: Node[] } => {
  const groupNodes: { [key: string]: Node[] } = {};
  for (const item of edge) {
    const key = item.getSourcePortId() as string;
    if (!groupNodes[key]) {
      groupNodes[key] = [];
    }
    groupNodes[key].push(item.getTargetCell() as Node);
  }
  return groupNodes;
};

export const getAllPortId = (cell: Cell | Node): { [key: string]: string } => {
  const portId: { [key: string]: string } = {};
  cell.getProp().ports.items.forEach((element: any) => {
    portId[element.name] = element.id;
  });
  return portId;
};

// Bool节点的nextPort是AND 或者 OR
export const nextWrapper = (
  graph: Graph,
  currentNode: ELJson | undefined,
  nextThenNodes: Node[] | Node | undefined,
): ELJson => {
  const execThenGroup: { [key: string]: ELJson[] } = {};
  const thenJson: ELCondition = {
    conditionType: ConditionType.then,
    executeType: ExecuteType.CONDITION,
    executableGroup: execThenGroup,
  };
  const defaltKey: ELJson[] = [];
  const finallyKeys: ELJson[] = [];
  const preKeys: ELJson[] = [];
  if (currentNode && ((isElCondition(currentNode) && !!currentNode.executableGroup) || isElNode(currentNode))) {
    if (isElCondition(currentNode) && currentNode.conditionType === ConditionType.finally) {
      finallyKeys.push(currentNode);
    } else if (isElCondition(currentNode) && currentNode.conditionType === ConditionType.pre) {
      preKeys.push(currentNode);
    } else {
      defaltKey.push(currentNode);
    }
  }
  execThenGroup.DEFAULT_KEY = defaltKey;
  if (!!finallyKeys) {
    execThenGroup.FINALLY_KEY = finallyKeys;
  }
  if (!!preKeys) {
    execThenGroup.PRE_KEY = preKeys;
  }
  if (!nextThenNodes) {
    return thenJson;
  }
  if (Array.isArray(nextThenNodes)) {
    nextThenNodes = nextThenNodes[0]
  }
  const data = nextThenNodes.getData() as NodeData;
  const elJson = data.buildNodeToJson(graph, nextThenNodes);
  if (!elJson) {
    return thenJson;
  }
  // 合并Next节点
  if (
    isElCondition(elJson) &&
    elJson.conditionType === ConditionType.then &&
    !elJson.tag
  ) {
    const nextKey = elJson.executableGroup.DEFAULT_KEY;
    if (nextKey) {
      defaltKey.push(...nextKey);
    }
    const finallyKey = elJson.executableGroup.FINALLY_KEY;
    if (finallyKey) {
      finallyKeys.push(...finallyKey);
    }
    const preKey = elJson.executableGroup.PRE_KEY;
    if (preKey) {
      preKeys.push(...preKey);
    }
  } else {
    defaltKey.push(elJson);
  }
  return thenJson;
};

export const andOrWrapper = (
  graph: Graph,
  currentNode: ELJson,
  nextChildrenNodes: Node[] | undefined,
): ELJson => {
  const execThenGroup: { [key: string]: ELJson[] } = {};
  const thenJson: ELCondition = {
    booleanConditionType: 'AND',
    conditionType: ConditionType.and_or_opt,
    executeType: ExecuteType.CONDITION,
    executableGroup: execThenGroup,
  };
  const defaltKey: ELJson[] = [];
  defaltKey.push(currentNode);
  if (!nextChildrenNodes) {
    return thenJson;
  }
  execThenGroup.AND_OR_ITEM = defaltKey;

  if (nextChildrenNodes.length === 1 && nextChildrenNodes[0]) {
    const data = nextChildrenNodes[0].getData() as NodeData;
    const elJson = data.buildNodeToJson(graph, nextChildrenNodes[0]);
    if (!elJson) {
      return thenJson;
    }
    // 合并AND节点
    if (
      elJson &&
      isElCondition(elJson) &&
      elJson.booleanConditionType === 'AND'
    ) {
      const andItem = elJson.executableGroup.AND_OR_ITEM;
      if (andItem) {
        defaltKey.push(...andItem);
      }
    } else {
      // 非AND节点，叶子节点或者OR节点
      defaltKey.push(elJson);
    }
    return thenJson;
  }

  // 多个是并
  const execWhenGroup: { [key: string]: ELJson[] } = {};
  const execWhenGroupList: ELJson[] = [];
  execWhenGroup.AND_OR_ITEM = execWhenGroupList;
  const orJson: ELCondition = {
    booleanConditionType: 'OR',
    conditionType: ConditionType.and_or_opt,
    executeType: ExecuteType.CONDITION,
    executableGroup: execWhenGroup,
  };

  nextChildrenNodes.forEach((item: Node) => {
    const data = item.getData() as NodeData;
    const elJson = data.buildNodeToJson(graph, item);
    if (elJson) {
      execWhenGroupList.push(elJson);
    }
  });

  // 多个是或
  defaltKey.push(orJson);
  return thenJson;
};

export const andOrWrapperV2 = (
  graph: Graph,
  nextChildrenNodes: Node[],
): ELJson => {
  const execThenGroup: { [key: string]: ELJson[] } = {};
  const thenJson: ELCondition = {
    booleanConditionType: 'AND',
    conditionType: ConditionType.and_or_opt,
    executeType: ExecuteType.CONDITION,
    executableGroup: execThenGroup,
  };
  const defaltKey: ELJson[] = [];
  execThenGroup.AND_OR_ITEM = defaltKey;

  if (nextChildrenNodes.length === 1 && nextChildrenNodes[0]) {
    const data = nextChildrenNodes[0].getData() as NodeData;
    const elJson = data.buildNodeToJson(graph, nextChildrenNodes[0]);
    if (!elJson) {
      return thenJson;
    }
    return elJson;
  }

  // 多个是并
  const execWhenGroup: { [key: string]: ELJson[] } = {};
  const execWhenGroupList: ELJson[] = [];
  execWhenGroup.AND_OR_ITEM = execWhenGroupList;
  const orJson: ELCondition = {
    booleanConditionType: 'OR',
    conditionType: ConditionType.and_or_opt,
    executeType: ExecuteType.CONDITION,
    executableGroup: execWhenGroup,
  };

  nextChildrenNodes.forEach((item: Node) => {
    const data = item.getData() as NodeData;
    const elJson = data.buildNodeToJson(graph, item);
    if (elJson) {
      execWhenGroupList.push(elJson);
    }
  });

  // 多个是或
  defaltKey.push(orJson);
  return thenJson;
};

// tag标签数据解析
// 使用tag，不使用id
export const tagNodeStr = (node: Node[] | undefined): string | undefined => {
  if (!node || node.length !== 1) return undefined;
  const nodeData = node[0]?.getData() as NodeData;
  return nodeData.liteInfo.cmpData?.tag;
};

export const whenWrapper = (graph: Graph, nodes: Node[]): ELJson => {
  // 多个是并
  const execWhenGroup: { [key: string]: ELJson[] } = {};
  const execWhenGroupList: ELJson[] = [];
  execWhenGroup.DEFAULT_KEY = execWhenGroupList;
  const orJson: ELCondition = {
    conditionType: ConditionType.when,
    executeType: ExecuteType.CONDITION,
    executableGroup: execWhenGroup,
  };
  nodes.forEach((item: Node) => {
    const data = item.getData() as NodeData;
    const elJson = data.buildNodeToJson(graph, item);
    if (elJson) {
      execWhenGroupList.push(elJson);
    }
  });
  return orJson;
};

export const thenWrapper = (graph: Graph, node: Node): ELJson | undefined => {
  const data = node.getData() as NodeData;
  const elJson = data.buildNodeToJson(graph, node);
  if (!elJson) return undefined;

  const execThenGroup: { [key: string]: ELJson[] } = {};
  const thenJson: ELCondition = {
    conditionType: ConditionType.then,
    executeType: ExecuteType.CONDITION,
    executableGroup: execThenGroup,
  };

  if (isElCondition(elJson)) {
    if (elJson.conditionType === ConditionType.finally) {
      execThenGroup.FINALLY_KEY = [elJson];
    } else if (elJson.conditionType === ConditionType.pre) {
      execThenGroup.PRE_KEY = [elJson];
    } else {
      return elJson;
    }
  }

  const defaltKey: ELJson[] = [];
  execThenGroup.DEFAULT_KEY = defaltKey;
  defaltKey.push(elJson);
  return thenJson;
};

/**
 * 解除最外层AND Condition
 * @param elJson 数据
 * @returns 
 */
export const unsealAndWrapper = (elJson: ELJson): ELJson => {
  if (isElCondition(elJson)
    && elJson.conditionType === ConditionType.and_or_opt
    && elJson.booleanConditionType === "AND"
    && elJson.executableGroup.AND_OR_ITEM.length === 1) {
    return elJson.executableGroup.AND_OR_ITEM[0];
  }
  return elJson;
}

export const unsealThenWrapper = (elJson: ELJson): ELJson => {
  if (isElCondition(elJson)
    && elJson.conditionType === ConditionType.then
    && elJson.executableGroup.DEFAULT_KEY.length === 1
    && !elJson.tag) {
    return elJson.executableGroup.DEFAULT_KEY[0];
  }
  return elJson;
}
