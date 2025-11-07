<script lang="ts" setup>
import type { ComponentPublicInstance } from 'vue';
import { onUnmounted, ref, shallowRef, watch } from 'vue';
import { getTeleport } from '@antv/x6-vue-shape';
import { ElButton, ElMessage } from 'element-plus';

// 假设您的 store 路径是这个
import { useAntvX6Store } from '/@/stores/operation';

// 导入其他本地组件
import InitData from './initData.vue';
import Reason from './reason.vue';
import RightPanel from './rightPanel.vue';
import UploadElJson from './uploadElJson.vue';
import { defaultCustomerData, initData } from './data';

const antvX6Store = useAntvX6Store();
const TeleportContainer = shallowRef<any>(null);

// 用于模板引用的 Ref
const graphContainer = ref<HTMLDivElement | null>(null);
const stencilContainer = ref<HTMLDivElement | null>(null);
const mapContainer = ref<HTMLDivElement | null>(null);

// 标记是否已成功初始化，防止任何意外的重复初始化
const isInitialized = ref(false);

// 使用 watch 侦听 DOM 元素是否准备就绪
// 这是处理异步或条件渲染下初始化第三方库的最稳健方法
const stopWatch = watch(
	[graphContainer, stencilContainer, mapContainer],
	([graphEl, stencilEl, mapEl]) => {
		// 确保两个元素都已存在，并且尚未初始化
		if (graphEl && stencilEl && !isInitialized.value) {
			initializeGraph(graphEl, stencilEl, mapEl);

			// 初始化完成后，立即停止 watch，避免不必要的性能开销
			stopWatch();
		}
	},
	{
		// 配置项：我们不使用 immediate，而是等待 ref 从 null 变为 DOM 元素
	}
);

// 异步初始化函数
async function initializeGraph(graphEl: HTMLElement, stencilEl: HTMLElement, mapEl: HTMLElement) {
	try {
		// 标记为已初始化，防止重复执行
		isInitialized.value = true;

		// 调用 store 的 reset，确保开始前环境是干净的
		antvX6Store.reset();

		await antvX6Store.initGraph(graphEl, mapEl);
		await antvX6Store.initStencil(stencilEl);

		const data = {
			nodes: [...initData.nodes, ...defaultCustomerData.nodes],
			models: [...initData.models, ...defaultCustomerData.models],
		};

		await antvX6Store.initNodeData(data);

		TeleportContainer.value = getTeleport();

		console.log('AntV X6 graph initialized successfully!');

	} catch (error) {
		console.error('Failed to initialize AntV X6 graph:', error);
		ElMessage.error('流程图编辑器初始化失败，请检查控制台错误信息。');
		// 如果初始化失败，重置标记，以便在修复问题后可以重试
		isInitialized.value = false;
	}
}

// onUnmounted 清理逻辑是解决页面切换导致应用崩溃的关键
onUnmounted(() => {
	console.log('AntV X6 component is being unmounted, cleaning up...');
	// 1. 停止 watch，以防组件在初始化完成前就被卸载
	stopWatch();
	// 2. 调用 store 的 reset 方法，这将调用 graph.dispose() 来移除所有全局监听器和实例
	antvX6Store.reset();
});

// --- 下面的 UI 交互逻辑保持不变 ---
interface ReasonExport {
	openDialog: () => void;
}
const initDataRef = ref<ComponentPublicInstance<ReasonExport>>();
const openInitData = () => {
	initDataRef.value?.openDialog();
};

const uploadElJsonRef = ref<ComponentPublicInstance<ReasonExport>>();
const openUploadElJson = () => {
	uploadElJsonRef.value?.openDialog();
};

const reasonRef = ref<ComponentPublicInstance<ReasonExport>>();
const openReason = () => {
	const graph = antvX6Store.getGraph();
	if (reasonRef.value && graph) {
		const nodes = graph.getNodes();
		if (nodes && nodes.length > 0) {
			reasonRef.value.openDialog();
		} else {
			ElMessage.error('请先添加节点');
		}
	}
};

const clearNodes = () => {
	antvX6Store.getGraph()?.clearCells();
};
</script>

<template>
	<div class="liteflow-editor-container">
		<component :is="TeleportContainer" />

		<div class="editor-main-layout">
			<!-- 左侧组件栏，添加 ref -->
			<div ref="stencilContainer" class="stencil-container"></div>

			<div class="content-container">
				<!-- 画布容器，添加 ref -->
				<div ref="graphContainer" class="antvX6-container"></div>
				<div ref="mapContainer" class="map-container"></div>

				<div class="operation-container">
					<div class="buttons">
						<ElButton color="#4d73e6" @click="openInitData"> 初始化Node </ElButton>
						<ElButton color="#4d73e6" @click="openUploadElJson"> 导入El </ElButton>
						<ElButton color="#4d73e6" @click="openReason"> 导出EL </ElButton>
						<ElButton color="#4d73e6" @click="clearNodes"> 清除 </ElButton>
					</div>
				</div>
			</div>
		</div>

		<RightPanel />
		<UploadElJson ref="uploadElJsonRef" />
		<InitData ref="initDataRef" />
		<Reason ref="reasonRef" />
	</div>
</template>

<style lang="scss" scoped>
// 根容器，定义了组件的边界和基本布局
.liteflow-editor-container {
	position: relative;
	width: 100%;
	height: 93vh; // 建议使用一个明确的高度
	border: 1px solid #e0e0e0;
	overflow: hidden;
	display: flex;
}

// 主布局容器
.editor-main-layout {
	display: flex;
	width: 100%;
	height: 100%;
}

// 左侧组件栏
.stencil-container {
	position: relative;
	flex-shrink: 0;
	width: 200px;
	height: 100%;
	box-shadow: 2px 0 8px 0 rgba(0, 0, 0, 0.05);
	z-index: 2; // 比画布高
}
.map-container {
	position: absolute;
	bottom: 60px;
	right: 120px;
	z-index: 10;
	width: 200px;
	height: 150px;
	background-color: #ffffff;
	border: 1px solid #e0e0e0;
	box-shadow: 0 4px 12px 0 rgba(0, 0, 0, 0.1);
	border-radius: 4px;
}
// 右侧主内容区域
.content-container {
	position: relative;
	flex: 1 1 0%;
	height: 100%;
	display: flex;
	flex-direction: column;
}

// AntV 画布容器
.antvX6-container {
	width: 100%;
	height: 100%;
}

// 底部操作按钮容器
.operation-container {
	position: absolute;
	bottom: 20px;
	left: 50%;
	transform: translateX(-50%);
	z-index: 10; // 确保在最上层
	display: flex;
	align-items: center;
	justify-content: center;
	padding: 0 20px;
	height: 50px;
	background-color: #ffffff;
	box-shadow: 0 4px 12px 0 rgba(0, 0, 0, 0.1);
	border-radius: 25px;

	.buttons {
		display: flex;
		align-items: center;
		gap: 15px;
	}
}

// AntV 深度样式覆盖
:deep(.x6-widget-stencil) {
	background-color: #f8f9fa;
}

:deep(.x6-widget-stencil-title) {
	background-color: #dde1e5;
}

:deep(.x6-widget-stencil-group-title) {
	background-color: #e9edf0;
}
</style>
