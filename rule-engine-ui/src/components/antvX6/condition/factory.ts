import { ELCondition, ELJson, ELNode, NodeShapes } from "../node/type";
import { elJsonToGraph as commonElJson } from './common';
import { elJsonToGraph as thenElJson } from './then';
import { elJsonToGraph as whenElJson } from './when';
import { elJsonToGraph as tagElJson } from './tag';
import { elJsonToGraph as ifElJson } from './if';
import { elJsonToGraph as catchElJson } from './catch';
import { elJsonPreToGraphCondition as preElJson, elJsonFinallyToGraphCondition as finallyElJsonCondition } from './preFinally';
import { elJsonToGraph as switchElJson, elJsonToGraphCondition as switchElJsonCondition } from './switch';
import { elJsonToGraph as iteratorElJson, elJsonToGraphCondition as iteratorElJsonCondition } from './iterator';
import { elJsonToGraph as forElJson, elJsonToGraphCondition as forElJsonCondition } from './for';
import { elJsonToGraph as boolElJson, elNotJsonToGraphCondition as boolNotElJsonCondition, elJsonToGraphCondition as boolElJsonCondition } from './bool';

import { X6EdgePortJson, X6GraphJson } from "./type";


export const elX6NodeGraph: Map<
    string,
    (graphJson: X6GraphJson, json: ELNode, pid: X6EdgePortJson | undefined) => { id: string } | undefined
> = new Map();
elX6NodeGraph.set(NodeShapes.COMMON, commonElJson);
elX6NodeGraph.set(NodeShapes.BOOLEAN, boolElJson);
elX6NodeGraph.set(NodeShapes.SWITCH, switchElJson);
elX6NodeGraph.set(NodeShapes.ITERATOR, iteratorElJson);
elX6NodeGraph.set(NodeShapes.FOR, forElJson);


export const elX6ConditionGraph: Map<
    string,
    (graphJson: X6GraphJson, json: ELCondition, pid: X6EdgePortJson | undefined) => { id: string } | undefined
> = new Map();
elX6ConditionGraph.set("then", thenElJson);
elX6ConditionGraph.set("when", whenElJson);
elX6ConditionGraph.set("and_or_opt", boolElJsonCondition);
elX6ConditionGraph.set("not_opt", boolNotElJsonCondition);
elX6ConditionGraph.set("if", ifElJson);
elX6ConditionGraph.set("switch", switchElJsonCondition);
elX6ConditionGraph.set("iterator", iteratorElJsonCondition);
elX6ConditionGraph.set("for", forElJsonCondition);
elX6ConditionGraph.set("catch", catchElJson);
elX6ConditionGraph.set("pre", preElJson);
elX6ConditionGraph.set("finally", finallyElJsonCondition);

export const elX6Graph: Map<
    string,
    (graphJson: X6GraphJson, json: ELJson, pid: X6EdgePortJson | undefined) => { id: string } | undefined
> = new Map();
elX6Graph.set("tag", tagElJson);

