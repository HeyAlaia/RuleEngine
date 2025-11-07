<script lang="ts" setup>
import type { NodeData } from '/@/components/antvX6/node/type';
import { ExecuteType } from '/@/components/antvX6/node/type';

import { ElDialog, ElTabPane, ElTabs, TabPaneName } from 'element-plus';

import { thenWrapper, whenWrapper } from '/@/components/antvX6/node/common';
import { useAntvX6Store } from '/@/stores/operation';

import { computed, ref } from 'vue';
import { useClipboard } from '@vueuse/core';
import { getJsonToEl } from '/@/api/liteflow/operation';
import { X6EdgeJson, X6GraphJson, X6NodeJson } from '/@/components/antvX6/condition/type';

import VueJsonPretty from 'vue-json-pretty';
import 'vue-json-pretty/lib/styles.css';

const show = ref(false);
const antvX6Store = useAntvX6Store();

const openDialog = () => {
	show.value = true;
	exportEl();
	tabChange;
};

const closeDialog = () => {
	show.value = false;
	elJson.value = '';
};

const elJson = ref();

const elEl = ref();
const { copied: elElCopy, copy: elElCopyFn } = useClipboard({ legacy: true, source: elEl.value });

const exportEl = () => {
	const rootNodes = antvX6Store
		.getGraph()
		.getNodes()
		.filter((item) => {
			return item.getData()?.isRootNode(antvX6Store.getGraph(), item);
		});

	if (!rootNodes || rootNodes.length === 0) {
		return;
	}

	if (rootNodes.length === 1 && rootNodes[0]) {
		const data = rootNodes[0].getData() as NodeData;

		const nextEl = data.buildNodeToJson(antvX6Store.getGraph(), rootNodes[0]);

		if (nextEl && nextEl.executeType === ExecuteType.NODE) {
			elJson.value = thenWrapper(antvX6Store.getGraph(), rootNodes[0]);
			return;
		}
		elJson.value = nextEl;
		return;
	}

	elJson.value = whenWrapper(antvX6Store.getGraph(), rootNodes);
};

const tabChange = async (tabName: TabPaneName) => {
	if (tabName === 'liteflowEl') {
		const data = await getJsonToEl(elJson.value);
		elEl.value = data;
	}
};

const antvX6Json = computed(() => {
	const antvJson = antvX6Store.getGraph().toJSON();
	const nodes: X6NodeJson[] = [];
	const edges: X6EdgeJson[] = [];
	antvJson.cells.forEach((item) => {
		if (item.shape == 'edge') {
			edges.push({
				id: item.id!!,
				shape: item.shape,
				source: item.source,
				target: item.target,
			});
		} else {
			nodes.push({
				id: item.id!!,
				shape: item.shape!!,
				cmpData: item.data?.liteInfo.cmpData,
			});
		}
	});
	const rs: X6GraphJson = {
		nodes: nodes,
		edges: edges,
	};

	return rs;
});

const activeName = ref('liteFlowJson');

defineExpose({
	openDialog,
	closeDialog,
});
</script>

<template>
	<div>
		<ElDialog v-model="show" title="输出EL" class="dialog-container" :draggable="true">
			<ElTabs v-model="activeName" type="border-card" class="el-tabs" :stretch="true" @tab-change="tabChange">
				<ElTabPane label="liteFlowJson" name="liteFlowJson" class="el-tab-pane">
					<vue-json-pretty :data="elJson" />
				</ElTabPane>
				<ElTabPane label="liteflowEl" name="liteflowEl" class="el-tab-pane">
						<div class="copy-button">
							<div class="copy" v-show="!elElCopy" @click="elElCopyFn(elEl)">复制</div>
							<div class="copied" v-show="elElCopy">已复制</div>
						</div>
						<div class="el-content-text">
							{{ elEl }}
						</div>
				</ElTabPane>
				<ElTabPane label="antvX6Json" name="antvX6Json" class="el-tab-pane">
					<vue-json-pretty :data="antvX6Json" />
				</ElTabPane>
			</ElTabs>
		</ElDialog>
	</div>
</template>

<style lang="scss" scoped>

.el-tab-pane {
	width: 100%;
	height: 500px;
	overflow: scroll;
}

.el-content-text{
	padding-top: 30px;
}

.copy-button {
	position: absolute;
	cursor: pointer;
	display: inline-block;
	padding: 10px;
	z-index: 5;
	right: 30px;
	font-family: Consolas, Menlo, Courier, monospace;
	font-size: 14px;
	color: hsl(var(--foreground));
	white-space: nowrap;
	color: hsl(var(--primary));

	.copied {
		opacity: 0.4;
	}
}
</style>
