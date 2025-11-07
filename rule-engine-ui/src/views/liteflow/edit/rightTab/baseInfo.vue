<script setup lang="ts">
import type { Api } from '@form-create/element-ui';
import formCreate from '@form-create/element-ui';

import type { NodeData } from '/@/components/antvX6/node/type';
import type { BuildFormCreate } from '/@/api/liteflow/operation';

import { onMounted, onUnmounted, ref } from 'vue';

import { Node } from '@antv/x6';
import { ElButton, ElEmpty } from 'element-plus';

import { useCurrNodeStore, useFormCreateStore } from '/@/stores/operation';

const useCurrNode = useCurrNodeStore();
const useFormCreate = useFormCreateStore();

const loading = ref(true);

const loadRule = (node: Node | undefined) => {
	if (!node) {
		loading.value = false;
		return;
	}
	const formCreate = getFormCreate(node);
	if (formCreate) {
		const nodeData = node.getData() as NodeData;
		// 数据组装
		if (nodeData.liteInfo.cmpData) {
			formCreateBindValue(formCreate, nodeData.liteInfo.cmpData);
		}
	}
	rule.value = formCreate;
	loading.value = false;
};

const formCreateBindValue = (formCreate: BuildFormCreate[], value: { [key: string]: any }) => {
	formCreate.forEach((item) => {
		if (value[item.field]) {
			item.value = value[item.field];
		}
	});
};

// 获取表单生成规则
const getFormCreate = (node: Node): BuildFormCreate[] | undefined => {
	const nodeData = node.getData() as NodeData;
	const storeCreateForm = useFormCreate.getFormCreate(nodeData.liteInfo.id);
	// 从缓存中加载
	if (storeCreateForm) {
		return storeCreateForm;
	}
	// 从远程中加载
	if (nodeData.liteInfo.id.startsWith('pippin')) {
		console.warn('nodeData', nodeData);
	}
};

const unsubscribe = useCurrNode.$subscribe((_, state) => {
	loadRule(state.currentSelectNode);
});

onMounted(() => {
	loadRule(useCurrNode.currentSelectNode);
});

onUnmounted(() => {
	unsubscribe();
});

// api
const fApi = ref<Api>();

// 表单生成规则
const rule = ref();

// 表单配置样式，操作等
const options = ref({
	form: {
		labelWidth: 120,
		size: 'default',
		labelPosition: 'left',
	},
	submitBtn: false,
	resetBtn: false,
});

const submit = () => {
	const formData = fApi.value?.formData();
	if (formData) {
		useCurrNode.setCurrentNodeData(formData);
	}
};

const save = () => {
	const formData = fApi.value?.formData();
	if (formData) {
		useCurrNode.setCurrentNodeData(formData);
	}
};

const cancel = () => {};
</script>

<template>
	<div class="base-info-container" v-loading="loading">
		<div v-if="rule && rule.length > 0" class="flex-container">
			<div class="form-create-container">
				<form-create v-model:api="fApi" :rule="rule" :option="options" />
			</div>
			<div class="button-container">
				<el-button @click="submit">提交</el-button>
				<ElButton @click="save">保存</ElButton>
				<ElButton @click="cancel">取消</ElButton>
			</div>
		</div>
		<div v-else class="no-data">
			<ElEmpty description="暂无数据" class="no-data" />
		</div>
	</div>
</template>

<style lang="scss" scoped>
.base-info-container {
	height: 100%;

	.flex-container {
		display: flex;
		flex-direction: column;
		height: 100%;
	}

	.form-create-container {
		flex: 0 1 100%;
		overflow: scroll;
	}

	.button-container {
		display: flex;
		align-items: center;
		justify-content: left;
		height: 80px;
	}

	.no-data {
		width: 100%;
		height: 100%;
	}
}
</style>
