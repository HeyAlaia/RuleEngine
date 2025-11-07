import type { VueShapeConfig } from '@antv/x6-vue-shape';

import type { NodeLFData } from './type';
import { NodeShapes } from './type';

import { shapeConfig as boolShape } from './bool';
import { shapeConfig as boolNotShape } from './bool/NotCondition';
import { shapeConfig as catchShape } from './catch';
import { shapeConfig as forShape } from './for';
import { shapeConfig as ifShape } from './if';
import { shapeConfig as iteratorShape } from './iterator';
import { shapeConfig as normalShape } from './normal';
import { shapeConfig as finallyShape } from './preFinally/finally';
import { shapeConfig as preShape } from './preFinally/pre';
import { shapeConfig as switchShape } from './switch';
import { shapeConfig as tagShape } from './tag';
import { shapeConfig as whenShape } from './when';
import { shapeConfig as whileShape } from './while';

export const registryNode: Map<string, (liteData: NodeLFData) => VueShapeConfig> = new Map();
registryNode.set(NodeShapes.BOOLEAN, boolShape);
registryNode.set(NodeShapes.BOOLEAN_NOT_CONDITION, boolNotShape);
registryNode.set(NodeShapes.COMMON, normalShape);
registryNode.set(NodeShapes.CATCH, catchShape);
registryNode.set(NodeShapes.IF, ifShape);
registryNode.set(NodeShapes.PRE, preShape);
registryNode.set(NodeShapes.FINALLY, finallyShape);
registryNode.set(NodeShapes.FOR, forShape);
registryNode.set(NodeShapes.WHILE, whileShape);

registryNode.set(NodeShapes.TAG, tagShape);
registryNode.set(NodeShapes.SWITCH, switchShape);
registryNode.set(NodeShapes.ITERATOR, iteratorShape);
registryNode.set(NodeShapes.WHEN, whenShape);
