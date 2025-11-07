import type { Stencil } from '@antv/x6-plugin-stencil';

import type { BuildFormCreate } from '/@/api/liteflow/operation';

import { ref } from 'vue';

import { Edge, Graph, Node, Options, Size } from '@antv/x6';
import { History } from '@antv/x6-plugin-history';
import { Keyboard } from '@antv/x6-plugin-keyboard';
import { Scroller } from '@antv/x6-plugin-scroller';
import { Selection } from '@antv/x6-plugin-selection';
import { register } from '@antv/x6-vue-shape';
import { defineStore } from 'pinia';

import { initEvent } from '/@/components/antvX6/event';
import { registryNode } from '/@/components/antvX6/node';
import { setCmpData, setSelected } from '/@/components/antvX6/node/common';
import { PippinStencil } from '/@/components/antvX6/stencil';
import { cloneFnJSON } from '@vueuse/core';
import { NodeData } from '/@/components/antvX6/node/type';
import { LiteFlowApi } from '/@/api/liteflow/operation';
import { MiniMap } from '@antv/x6-plugin-minimap';
/**
 * @description: antvX6 store
 */
export const useAntvX6Store = defineStore('antvX6', () => {
	const graph = ref<Graph>();
	const stencil = ref<Stencil>();
	const stencilData = ref<{ [key: string]: Node<Node.Properties>[] }>({});
	const nodesData = ref<{ [key: string]: LiteFlowApi.LiteFlowNode }>({});
	const nodeSize = ref<{ [key: string]: Size }>({});

	/**
	 * 初始化 antvX6
	 * @param antvX6Id  antvX6 容器 id
	 */
	async function initGraph(container: HTMLElement, mapContainer: HTMLElement) {
		const x6 = new Graph({
			container: container, // 设置图实例的容器
			grid: true, // 显示网格
			mousewheel: {
				enabled: true, // 启用滚轮缩放
				modifiers: 'ctrl', // 按住 Ctrl 键时才启用缩放，默认是无须任何修饰键
				factor: 1.1, // 缩放因子
				minScale: 0.5, // 最小缩放比例
				maxScale: 3, // 最大缩放比例
			}, // 启用鼠标滚轮缩放,
			background: {
				color: '#efeff4', // 设置背景颜色
			},
			connecting: {
				// 连线配置
				snap: true, // 启用吸附
				allowMulti: true, // 允许创建多条边
				allowBlank: false, // 不允许连接到空白区域
				allowLoop: false, // 是否允许创建循环连线，即边的起始节点和终止节点为同一节点
				allowNode: false, // 是否允许边连接到节点（非节点上的连接桩）
				allowEdge: false, // 是否允许边连接到另一个边
				highlight: true, // 启用高亮
				router: {
					name: 'metro',
				},
				validateConnection: (_: Options.ValidateConnectionArgs) => {
					return true;
				},
			},
			highlighting: {},
		});

		// 注册键盘
		x6.use(
			new Keyboard({
				enabled: true,
				global: true,
			})
		);

		x6.use(
			new Selection({
				enabled: true,
				multiple: false,
				rubberband: true,
				movable: true,
				showNodeSelectionBox: true,
				showEdgeSelectionBox: true,
				pointerEvents: 'none',
				modifiers: 'ctrl',
				className: 'lite-flow-select',
			})
		);

		x6.use(
			new Scroller({
				pannable: {
					enabled: true,
					eventTypes: ['leftMouseDown'],
				},
				enabled: true,
				autoResize: true,
				pageVisible: false,
				pageBreak: false,
				minVisibleWidth: 0,
				minVisibleHeight: 0,
				modifiers: 'alt',
			})
		);

		x6.use(
			new History({
				enabled: true,
				ignoreChange: false,
			})
		);

		x6.use(
			new MiniMap({
				container: mapContainer,
			}),
		)

		// 注册事件
		initEvent(x6);

		graph.value = x6;
	}

	/**
	 * 初始化 stencil
	 * @param antvX6StencilId Stencil 容器 id
	 */
	async function initStencil(container: HTMLElement) {
		const st = new PippinStencil({
			title: '拖拽组件',
			target: graph.value,
			search(cell: Node, keyword: string) {
				return cell.shape.includes(keyword);
			},
			placeholder: '组件搜索',
			notFoundText: '未找到对应组件',
			stencilGraphHeight: 0,
			layoutOptions: {
				columns: 1,
				dx: 55,
				rowHeight: 50,
			},
			groups: [],
		});
		const sc = container;
		sc.replaceChildren(st.container);
		stencil.value = st;
	}

	// 初始化节点数据
	async function initNodeData(vo: LiteFlowApi.StencilVO) {
		if (!!stencilData.value && stencil.value) {
			Object.entries(stencilData.value).forEach(([model, nodes]) => {
				stencil.value?.unload(nodes, model);
			});
			stencil.value?.removeGroup(Object.keys(stencilData.value));
			stencilData.value = {};
		}
		// 初始化画布节点
		if (graph.value) {
			vo.nodes.forEach((node: LiteFlowApi.NodeVO) => {
				const shapeConfig = registryNode.get(node.type);
				if (shapeConfig) {
					// 画布注册节点
					console.log(node.description);
					register(shapeConfig({ type: node.type, label: node.label, id: node.id, description: node.description }));
				}
			});
		}

		// 初始化 stencil 节点
		if (graph.value && stencil.value) {
			// 分组注册
			vo.models.forEach((model: LiteFlowApi.ModelVO) => {
				stencil.value?.addGroup({ name: model.model, title: model.label });
			});

			const groupNodes: { [key: string]: LiteFlowApi.NodeVO[] } = {};
			// 节点分组
			for (const item of vo.nodes) {
				const key = item.model;
				if (!groupNodes[key]) {
					groupNodes[key] = [];
				}
				groupNodes[key].push(item);
			}

			// 侧边节点注册
			Object.entries(groupNodes).forEach(([model, nodes]) => {
				const cNodes = nodes
					.map((node: LiteFlowApi.NodeVO) => {
						const newNode = graph.value?.createNode({ shape: node.id });
						const newNodeData = newNode?.getData() as NodeData;
						nodeSize.value[node.id] = {
							width: newNodeData.x6Info.width!!,
							height: newNodeData.x6Info.height!!,
						};
						return newNode;
					})
					.filter((node) => !!node);
				if (cNodes) {
					stencil.value?.load(cNodes, model);
				}
				stencilData.value[model] = cNodes;
			});
		}

		// 构建 cmpDataFormCreate
		const formCreate = useFormCreateStore();
		vo.nodes.forEach((node: LiteFlowApi.NodeVO) => {
			if (node.cmpDataFormCreate) {
				formCreate.addFormCreate(node.id, node.cmpDataFormCreate);
			}
		});

		vo.nodes.forEach((node: LiteFlowApi.NodeVO) => {
			nodesData.value[node.id] = {
				id: node.id,
				type: node.type,
				clazz: node.clazz,
			};
		});
	}

	function createNode(nodeMetadata: Node.Metadata): Node<Node.Properties> | undefined {
		return graph.value?.createNode(nodeMetadata);
	}

	function addNode(nodeMetadata: Node.Metadata): Node<Node.Properties> | undefined {
		return graph.value?.addNode(nodeMetadata);
	}

	function addEdge(edges: Edge.Metadata[] | Edge.Metadata) {
		if (Array.isArray(edges)) {
			edges.forEach((edge) => {
				return graph.value?.addEdge(edge);
			});
		} else {
			return graph.value?.addEdge(edges);
		}
	}

	function fromJSON(model: { nodes?: Node.Metadata[]; edges?: Edge.Metadata[] }) {
		graph.value?.fromJSON(model);
		// 文档中有问题，这里需要手动触发
		model.nodes?.forEach((cell) => {
			const cellById = graph.value?.getCellById(cell.id!!);
			if (cellById) {
				graph.value?.trigger('node:added', { node: cellById });
			}
		});
	}

	function getGraph(): Graph {
		return graph.value as Graph;
	}

	function getNodesData(): LiteFlowApi.LiteFlowNode[] {
		return Object.values(nodesData.value);
	}

	function getType(shape: string): string {
		return nodesData.value[shape]?.type || '';
	}

	function getSize(shape: string): Size | undefined {
		return nodeSize.value[shape];
	}
	function reset() {
		// 确保实例存在且有 dispose 方法再调用
		if (graph.value && typeof graph.value.dispose === 'function') {
			graph.value.dispose();
		}
		// 将所有状态重置为初始值
		graph.value = undefined;
		stencil.value = undefined;
		stencilData.value = {};
		nodesData.value = {};
		nodeSize.value = {};
		console.log('AntV X6 store has been reset and instances disposed.');
	}
	return {
		getGraph,
		initGraph,
		initStencil,
		initNodeData,
		createNode,
		addNode,
		addEdge,
		fromJSON,
		getNodesData,
		getType,
		getSize,
		reset,
	};
});

export const useCurrNodeStore = defineStore('currNode', () => {
	const currentSelectNode = ref<Node>();

	function setCurrentNode(node: Node | undefined) {
		// 移除节点取消选中
		if (currentSelectNode.value) {
			setSelected(currentSelectNode.value, false);
		}
		// 传入节点被选中
		if (node) {
			setSelected(node, true);
		}
		currentSelectNode.value = node;
	}

	function setCurrentNodeData(data: { [key: string]: any }) {
		if (currentSelectNode.value) {
			setCmpData(currentSelectNode.value, data);
		}
	}

	return {
		setCurrentNode,
		setCurrentNodeData,
		currentSelectNode,
	};
});

export const useFormCreateStore = defineStore('formCreate', () => {
	const formCreate = ref<{ [key: string]: BuildFormCreate[] }>({});

	function addFormCreate(key: string, buildFormCreate: BuildFormCreate[]) {
		formCreate.value[key] = buildFormCreate;
	}

	function getFormCreate(key: string) {
		if (formCreate.value[key]) {
			return cloneFnJSON(formCreate.value[key]);
		}
		return [];
	}

	return {
		addFormCreate,
		getFormCreate,
	};
});
