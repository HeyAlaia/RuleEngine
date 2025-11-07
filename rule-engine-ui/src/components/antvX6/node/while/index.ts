import type { Graph, Node } from '@antv/x6';
import type { VueShapeConfig } from '@antv/x6-vue-shape';

import type { ELCondition, ELJson, NodeData, NodeLFData } from '../type';
import { ConditionType, ExecuteType, NodePortName } from '../type';

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
import { X6NodeJson } from '../../condition/type';

const DO_PORT = 'doPort';
const BREAK_PORT = 'breakPort';
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
			color: '#D133FF',
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

	// while 返回
	const whileGroup: { [key: string]: ELJson[] } = {};
	const whileJson: ELCondition = {
		conditionType: ConditionType.while,
		executeType: ExecuteType.CONDITION,
		executableGroup: whileGroup,
	};

	// 设置tag
	const tagPortId = portIds[NodePortName.TAG_PORT] ?? '';
	// 获取后继节点的所有子节点
	const tagnodes = childrenNodes[tagPortId];
	const tag = tagNodeStr(tagnodes);
	if (tag) {
		whileJson.tag = tag;
	}

	// bool port 端点
	const boolKeyGroupList = [] as ELJson[];
	whileGroup.WHILE_KEY = boolKeyGroupList;
	// 获取后继节点的portId
	const boolPortId = portIds[BOOL_PORT] ?? '';

	// 获取后继节点的所有子节点
	const boolNodes = childrenNodes[boolPortId];
	if (!boolNodes) {
		return undefined;
	}
	boolKeyGroupList.push(andOrWrapperV2(graph, boolNodes));

	// do port 端点
	const doPortId = portIds[DO_PORT] ?? '';
	const doNodes = childrenNodes[doPortId];
	if (doNodes) {
		const doKey = thenWrapper(graph, doNodes[0]);
		if (doKey) {
			whileGroup.DO_KEY = [unsealThenWrapper(doKey)];
		}
	}

	// break port 端点
	const breakKeyGroupList = [] as ELJson[];
	const breakPortId = portIds[BREAK_PORT] ?? '';
	const breakNodes = childrenNodes[breakPortId];
	if (breakNodes) {
		breakKeyGroupList.push(andOrWrapperV2(graph, breakNodes));
		whileGroup.BREAK_KEY = breakKeyGroupList;
	}

	// 获取后继节点的portId
	const nextPortId = portIds[NodePortName.NEXT_PORT] ?? '';

	// 获取后继节点的所有子节点
	const nextNodes = childrenNodes[nextPortId];
	return nextWrapper(graph, whileJson, nextNodes);
};

// const buildJsonToNode = (graph: Graph, node: Node) => {};

// 么有父节点时候，是rootNode
const isRootNode = (graph: Graph, node: Node) => {
	const childNode = getParentNodes(graph, node);
	return !childNode || childNode.length === 0;
};

export const positionNode = (nodes: { [key: string]: X6NodeJson[] }): { top: number; button: number } => {
	return {
		top: 0,
		button: 30,
	};
};
