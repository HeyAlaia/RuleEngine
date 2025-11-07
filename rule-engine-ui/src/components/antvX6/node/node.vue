<script lang="ts" setup>
import type { NodeData, NodeX6Data } from './type';

import { computed, inject, onMounted, reactive } from 'vue';

import { TinyColor } from '@ctrl/tinycolor';
import { ElTooltip } from 'element-plus';

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
});

const toolTipDisabled = computed(() => {
  return data.name && data.name !== '' && data.name.length >= 10;
});

const content = computed(() => {
  return data.name && data.name !== '' ? data.name : data.label;
});

onMounted(() => {
  if (getNode) {
    const node = getNode();
    const nodeStore = node.store.data;
    changeData(nodeStore.data);
    node.on('change:data', (e: any) => {
      changeData(e.current);
    });
  }
});

const changeData = (nodeData: NodeData) => {
	console.log(nodeData);
  // 节点名称
  if (nodeData.liteInfo) {
    data.label = nodeData.liteInfo.label;
    if (nodeData.liteInfo.description) {
      data.desc = nodeData.liteInfo.description;
    }
  }
  if (nodeData.x6Info) {
    data.x6Info = nodeData.x6Info;
    data.darkenColor = new TinyColor(nodeData.x6Info.color)
      .darken(10)
      .toHexString();
    data.lightenColor = new TinyColor(nodeData.x6Info.color)
      .lighten(10)
      .toHexString();
  }
};
</script>

<template>
	<div :style="{
    '--header-bg-color': data.lightenColor,
    '--sign-color': data.darkenColor,
  }">
		<!-- 默认未激活时 (Stencil) - 移除 Tooltip, 优化为纯文本卡片 -->
		<div v-if="!data.x6Info.activated" class="nodeContainer node-stencil">
			<div class="node">
				<div class="label">
					{{ content }}
				</div>
			</div>
		</div>

		<!-- 激活后 - 移除 Tooltip, 改为完整的 Card 结构 -->
		<div v-else :class="{ 'node-select': data.x6Info.selected }" class="nodeContainer node-active" :style="{
      '--active-width': data.x6Info.width,
      '--active-height': data.x6Info.height,
    }">
			<div class="node">
				<div class="header">
					<div class="index"></div>
					<span class="label">{{ data.label }}</span>
				</div>
				<!-- 将 desc 改为 body，更符合 Card 语义 -->
				<div class="body" v-if="!!data.desc">
					{{ data.desc }}
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
	box-shadow: 0 1px 3px rgba(0, 0, 0, 0.02); // 非常 subtle 的阴影
	transition: border-color var(--transition-speed) ease, box-shadow var(--transition-speed) ease;
	overflow: hidden; // 确保内部圆角正确
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
 * 2. Stencil 节点样式 (最大化文字区域)
 **********************************************/
.node-stencil {
	width: 160px;
	/* 关键：使用 padding 和 line-height 控制尺寸，而不是固定 height */
	padding: 8px 12px;
	display: flex;
	align-items: center;
	min-height: 40px; // 保持一个最小高度

	.node {
		width: 100%;
	}

	.label {
		font-size: 14px;
		color: var(--text-color-primary);
		overflow: hidden;
		white-space: nowrap;
		text-overflow: ellipsis;
	}
}


/**********************************************
 * 3. 画布中的 Active 节点样式 (标准 Card 结构)
 **********************************************/
.node-active {
	width: calc(var(--active-width) * 1px);
	height: calc(var(--active-height) * 1px);

	.node {
		display: flex;
		flex-direction: column; // 垂直布局 header 和 body
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
	height: 36px; // 固定的头部高度
	flex-shrink: 0; // 防止头部被压缩
	font-weight: 500;
	color: var(--text-color-primary);
	/* 关键：添加头部背景色和底部分割线 */
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
		overflow: hidden;
		white-space: nowrap;
		text-overflow: ellipsis;
	}
}

/* Card Body 样式 (原 .desc) */
.body {
	box-sizing: border-box;
	padding: 12px;
	font-size: 12px;
	color: var(--text-color-regular);
	line-height: 1.5;
	flex-grow: 1;  // 占据所有剩余空间
	overflow-y: auto; // 如果内容过多，允许滚动
}
</style>