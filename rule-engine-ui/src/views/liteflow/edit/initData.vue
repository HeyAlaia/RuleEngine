<script lang="ts" setup>
import { ref } from 'vue';

import { ElButton, ElDialog, ElInput } from 'element-plus';
import { useAntvX6Store } from '/@/stores/operation';
import { defaultCustomerData, initData } from './data';

const show = ref(false);

const textarea = ref(
	JSON.stringify(
		{
			nodes: [...defaultCustomerData.nodes],
			models: [...defaultCustomerData.models],
		},
		null,
		2
	)
);

const openDialog = () => {
	show.value = true;
};

const antvX6Store = useAntvX6Store();

const initDataFn = async () => {
	if (!!textarea.value) {
		const json = JSON.parse(textarea.value);
		if (!!json.nodes && !!json.models) {
			const data = {
				nodes: [...initData.nodes, ...json.nodes],
				models: [...initData.models, ...json.models],
			};

			await antvX6Store.initNodeData(data);
		}
	}
};

const desc = `{
    "nodes": [
        {
            "id": "boolNode",
            "type": "boolean",
            "label": "测试bool",
            "model": "default",
            "description": "string"
        },
        ....
    ],
    "models": [
        {
            "label": "默认节点",
            "model": "default",
            "description": "当未进行初始化时使用默认节点"
        }
    ]
}`;

defineExpose({
	openDialog,
	textarea,
});
</script>

<template>
	<div>
		<ElDialog v-model="show" title="初始化 NODE" class="dialog-container">
			<div class="data-dialog-body">
				<ElInput v-model="textarea" :style="{ height: '100%' }" input-style="height: 100%" type="textarea" resize="none" :placeholder="desc" />
				<div class="init-button">
					<ElButton @click="initDataFn">初始化</ElButton>
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
