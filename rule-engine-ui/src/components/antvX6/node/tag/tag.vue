<script lang="ts" setup>
import { computed, inject, onMounted, reactive } from 'vue';

import { TinyColor } from '@ctrl/tinycolor';
import { ElTooltip } from 'element-plus';
import { NodeData, NodeLFData, NodeX6Data } from '../type';

const getNode = inject<any>('getNode');

const data = reactive({
	darkenColor: '#fff',
	lightenColor: '#fff',
	status: 'default',
	label: '普通组件',
	desc: '',
	name: '',
	visibility: false,
	x6Info: {} as NodeX6Data,
	liteInfo: {} as NodeLFData,
});

const toolTipDisabled = computed(() => {
	return data.name && data.name !== '' && data.name.length >= 10;
});

const content = computed(() => {
	return data.name && data.name !== '' ? data.name : data.label;
});

const tagData = computed(() => {
	let res = '暂无数据';
	if (data.liteInfo.cmpData && !!data.liteInfo.cmpData.tag) {
		return data.liteInfo.cmpData.tag;
	}
	return res;
});

onMounted(() => {
	if (getNode) {
		const node = getNode();
		const nodeStore = node.store.data;
		changeData(nodeStore.data);
		node.on('change:data', (e: any) => {
			console.log('change:data', e);
			changeData(e.current);
		});
	}
});

const changeData = (nodeData: NodeData) => {
	// 节点名称
	if (nodeData.liteInfo) {
		data.label = nodeData.liteInfo.label;
		if (nodeData.liteInfo.description) {
			data.desc = nodeData.liteInfo.description;
		}
		data.liteInfo = nodeData.liteInfo;
	}
	if (nodeData.x6Info) {
		data.x6Info = nodeData.x6Info;
		data.darkenColor = new TinyColor(nodeData.x6Info.color).darken(10).toHexString();
		data.lightenColor = new TinyColor(nodeData.x6Info.color).lighten(10).toHexString();
	}
};
</script>

<template>
	<div :style="{
        '--header-bg-color': data.lightenColor,
        '--sign-color': data.darkenColor,
    }">
		<!-- 默认未激活时 (Stencil) -->
		<div v-if="!data.x6Info.activated" class="nodeContainer node-stencil">
			<div class="node">
				<div class="label">
					{{ content }}
				</div>
			</div>
		</div>

		<!-- 激活后 -->
		<div v-else :class="{ 'node-select': data.x6Info.selected }" class="nodeContainer node-active" :style="{
            '--active-width': data.x6Info.width,
            '--active-height': data.x6Info.height,
        }">
			<div class="node">
				<div class="header">
					<div class="index"></div>
					<span class="label">{{ data.label }}</span>
				</div>
				<!-- 将 .desc.tag 语义化为 .body -->
				<div class="body">
					{{ tagData }}
				</div>
			</div>
		</div>
	</div>
</template>

<style lang="scss" scoped>
/*
  CSS 变量定义
*/
:root {
	--sign-color: #6eeeee;
	--active-width: 300;
	--active-height: 100;
	--transition-speed: 0.2s;
	--primary-color: #409eff;
	--border-color: #e4e7ed;
	--header-bg-color: #f9fafb; // ElCard 头部标准背景色
	--text-color-primary: #303133;
	--text-color-regular: #606266;
}

/**********************************************
 * 1. 统一的 Card 基础样式 (ElCard Core)
 **********************************************/
.nodeContainer {
	background-color: #fff;
	border: 1px solid var(--border-color);
	border-radius: 4px;
	cursor: pointer;
	box-shadow: 0 1px 3px rgba(0, 0, 0, 0.02); // 极其细微的阴影
	transition: border-color var(--transition-speed) ease, box-shadow var(--transition-speed) ease;
	overflow: hidden; // 保证内部元素不超出圆角
}

.nodeContainer:hover {
	border-color: var(--primary-color);
	box-shadow: 0 1px 5px rgba(0, 0, 0, 0.05);
}

.node-select,
.node-select:hover {
	border-color: var(--primary-color);
	box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2); // 选中时的辉光效果
}


/**********************************************
 * 2. Stencil 节点样式 (专注文字展示)
 **********************************************/
.node-stencil {
	width: 160px;
	padding: 8px 12px; // 使用 padding 定义内边距
	display: flex;
	align-items: center;
	min-height: 40px; // 设定最小高度以保持优雅

	.node {
		width: 100%;
	}

	.label {
		font-size: 14px;
		color: var(--text-color-primary);
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
	}
}


/**********************************************
 * 3. 画布中的 Active 节点样式 (标准 Card 结构)
 **********************************************/
.node-active {
	width: calc(var(--active-width) * 1px);
	height: calc(var(--active-height) * 1.5px);

	.node {
		display: flex;
		flex-direction: column; // 垂直分布 header 和 body
		width: 100%;
		height: 100%;
	}
}

/* Card Header 样式 */
.header {
	box-sizing: border-box;
	display: flex;
	align-items: center;
	padding: 0 12px;
	height: 40px; // 固定的头部高度，比百分比更可靠
	flex-shrink: 0; // 防止被 flex 布局压缩
	font-weight: 500;
	color: var(--text-color-primary);

	/* ElCard 风格的头部：背景色 + 底部分割线 */
	background-color: var(--header-bg-color);
	border-bottom: 1px solid var(--border-color);

	.index {
		width: 8px;
		height: 8px;
		margin-right: 8px;
		background-color: var(--sign-color);
		border-radius: 50%;
		flex-shrink: 0;
	}

	.label {
		white-space: nowrap;
		overflow: hidden;
		text-overflow: ellipsis;
	}
}

/* Card Body 样式 (原 .desc.tag) */
.body {
	box-sizing: border-box;
	padding: 10px 12px;
	font-size: 12px;
	color: var(--text-color-regular);
	line-height: 1.5;
	flex-grow: 1;  // 占据所有剩余的垂直空间
	overflow-y: auto; // 当内容超出时，允许内部滚动
}
</style>