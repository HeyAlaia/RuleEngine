<script lang="ts" setup>
import { ref } from 'vue';

import { ElButton, ElDialog, ElInput, ElOption, ElSelect } from 'element-plus';
import { getElToJson } from '/@/api/liteflow/operation';
import { doElJsonToGraph } from '/@/components/antvX6/condition';
import { DagreLayout, Model, OutNode } from '@antv/layout';
import { useAntvX6Store } from '/@/stores/operation';
import { X6GraphJson } from '/@/components/antvX6/condition/type';
import { Model as X6Model, Node } from '@antv/x6';
import { ELJson } from '/@/components/antvX6/node/type';

const antvX6Store = useAntvX6Store();
const show = ref(false);

const textarea = ref();

const openDialog = () => {
	show.value = true;
};

const liteflowJsonToX6 = (elJson: ELJson): X6GraphJson => {
	const x6GraphJson: X6GraphJson = { nodes: [], edges: [] };
	doElJsonToGraph(x6GraphJson, elJson, undefined);
	return x6GraphJson;
};

const liteFlowElToJson = async (value: string): Promise<any> => {
	const res = await getElToJson(value, antvX6Store.getNodesData());
	return res;
};

const initDataFn = async () => {
	const input = textarea.value;
	if (!input) {
		return;
	}
	let x6Json: X6GraphJson;
	if (select.value === 'liteflowEl') {
		const res = await liteFlowElToJson(input);
		x6Json = liteflowJsonToX6(res);
	} else if (select.value === 'liteflowJson') {
		x6Json = liteflowJsonToX6(input);
	} else {
		x6Json = JSON.parse(input);
	}
	const fromJsonData: X6Model.FromJSONData = {
		nodes: x6Json.nodes.map((x) => {
			let data = {};
			if (x.cmpData) {
				data = {
					liteInfo: {
						cmpData: x.cmpData,
					},
				};
			}
			return {
				id: x.id,
				shape: x.shape,
				data: data,
			};
		}),
		edges: x6Json.edges,
	};
	antvX6Store.fromJSON(fromJsonData);

	const data: Model = {
		nodes: x6Json.nodes.map((x) => {
			return {
				id: x.id,
				shape: x.shape,
				size: [300, 100],
				width: 300,
				height: 100,
			};
		}),
		edges: x6Json.edges.map((x) => {
			return {
				...x,
				source: x.source.cell,
				target: x.target.cell,
			};
		}),
	};

	const dagreLayout = new DagreLayout({
		type: 'dagre',
		rankdir: 'LR',
		align: 'UL',
		ranksep: 30,
		nodesep: 15,
		controlPoints: true,
	});
	dagreLayout.layout(data);
	data.nodes?.forEach((n) => {
		const node = n as OutNode;
		const nodeById = antvX6Store.getGraph().getCellById(node.id) as Node;
		nodeById.setPosition(node.x, node.y);
	});
	dagreLayout.destroy();
};

const select = ref('liteflowEl');
const selectOption = ref([
	{
		value: 'liteflowEl',
		label: 'liteflowEl',
	},
	{
		value: 'liteFlowJson',
		label: 'liteFlowJson',
	},
	{
		value: 'antvX6Json',
		label: 'antvX6Json',
	},
]);
defineExpose({
	openDialog,
	textarea,
});
</script>

<template>
	<div>
		<ElDialog v-model="show" title="el构建节点" class="dialog-container">
			<div class="data-dialog-body">
				<ElInput v-model="textarea" :style="{ height: '100%' }" input-style="height: 100%" type="textarea" resize="none" placeholder="请输入el" />
				<div class="init-button">
					<ElSelect v-model="select" placeholder="Select" size="large">
						<ElOption v-for="item in selectOption" :key="item.value" :label="item.label" :value="item.value" />
					</ElSelect>
					<ElButton @click="initDataFn">构建节点</ElButton>
				</div>
			</div>
		</ElDialog>
	</div>
</template>
<style lang="scss" scoped>
:deep(.dialog-container) {
	height: 50%;
	overflow: hidden;

	.el-dialog__body {
		height: 90%;
	}
}

.data-dialog-body {
	height: 100%;
	display: flex;
	flex-direction: column;
	justify-content: space-between;

	.init-button {
		height: 50px;
		display: flex;
		justify-content: flex-end;
		align-items: center;
	}
}
</style>
