<script lang="ts" setup>
import type { NodeData } from '/@/components/antvX6/node/type';

import { onUnmounted, ref } from 'vue';

import { ElDrawer, ElTabPane, ElTabs } from 'element-plus';

import { useCurrNodeStore } from '/@/stores/operation';

import BaseInfo from './rightTab/baseInfo.vue';

const show = ref(false);
const title = ref('');

const useCurrNode = useCurrNodeStore();

const unsubscibe = useCurrNode.$subscribe((_, state) => {
	show.value = state.currentSelectNode !== undefined;
	if (state.currentSelectNode) {
		const nodeData = state.currentSelectNode.getData() as NodeData;
		title.value = nodeData.liteInfo.label;
	}
});

onUnmounted(() => {
	unsubscibe();
});

const close = () => {
	useCurrNode.setCurrentNode(undefined);
};

const activeName = ref('baseInfo');
</script>

<template>
	<div>
		<ElDrawer v-model="show" :title="title" @close="close" :modal="false" :close-on-click-modal="false" modal-class="info-panel-modal">
			<ElTabs v-model="activeName" class="info-tabs">
				<ElTabPane label="基础信息" name="baseInfo" class="info-tab-pane">
					<BaseInfo />
				</ElTabPane>
				<!-- <ElTabPane label="ElJson" name="elJson" class="info-tab-pane">
          <ElJson />
        </ElTabPane> -->
			</ElTabs>
		</ElDrawer>
	</div>
</template>

<style lang="scss" scoped>
:deep(.info-panel-modal .el-drawer) {
	top: 5%;
	bottom: 0;
	height: 90%;
	border-radius: 15px 0 0 15px;
}

.info-tabs {
	height: 100%;

	.info-tab-pane {
		height: 100%;
	}
}
</style>
