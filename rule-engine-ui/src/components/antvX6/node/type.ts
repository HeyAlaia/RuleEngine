import type { Graph, Node } from '@antv/x6';

export interface NodeX6Data {
  selected: boolean;
  activated: boolean;
  error?: string;
  width?: number;
  height?: number;
  color: string;
}

export interface NodeLFData {
  id: string; // 唯一标识，执行的beanName
  clazz?: string; // 区分当前使用 全类名
  type: string; // 原始类型
  label: string; // 节点名称 展示
  description?: string; // 节点描述 展示
  cmpData?: { [key: string]: any }; // 组件数据
}

// el Json 的返回结果
export interface ELJson {
  executeType: string;
  id?: string;
  tag?: string;
}

export interface ELNode extends ELJson {
  id: string; // Node 才有，
  type: string; // Node 才有，节点类型
  cmpData?: { [key: string]: any }; // 组件数据
  //JsonToEl字段
  label?: string; // Node 才有，节点名称 展示
  description?: string; // Node 才有，节点描述 展示
}

export interface ELCondition extends ELJson {
  executableGroup: { [key: string]: ELJson[] }; // condition 才有
  conditionType: string; // condition 才有，条件类型
  booleanConditionType?: string; // boolean_condition 才有，条件类型
}

export interface NodeData {
  buildNodeToJson(graph: Graph, node: Node): ELJson | undefined;
  isRootNode(graph: Graph, node: Node): boolean;
  x6Info: NodeX6Data;
  liteInfo: NodeLFData;
}

export enum ExecuteType {
  CONDITION = 'CONDITION',
  NODE = 'NODE',
}

export enum ConditionType {
  and_or_opt = 'and_or_opt',
  catch = 'catch',
  finally = 'finally',
  for = 'for',
  if = 'if',
  iterator = 'iterator',
  not_opt = 'not_opt',
  pre = 'pre',
  switch = 'switch',
  then = 'then',
  when = 'when',
  while = 'while',
}

export enum NodeShapes {
  BOOLEAN = 'boolean',
  BOOLEAN_NOT_CONDITION = 'booleanNotCondition',
  BOOLEAN_SCRIPT = 'boolean_script',
  CATCH = 'catch',
  COMMON = 'common',
  FINALLY = 'finally',
  FOR = 'for',
  FOR_SCRIPT = 'for_script',
  IF = 'if',
  ITERATOR = 'iterator',
  PRE = 'pre',
  SCRIPT = 'script',
  SWITCH = 'switch',
  SWITCH_SCRIPT = 'switch_script',
  TAG = 'tag',
  WHEN = 'when',
  WHILE = 'while',
}

export enum NodePortName {
  BEFORE_PORT = 'beforePort',
  NEXT_PORT = 'nextPort',
  TAG_PORT = 'tagPort',
}
