import request from '/@/utils/request';

export interface BuildFormCreate {
	title: string;
	name?: string;
	type: string;
	field: string;
	value?: any;
	errorMsg?: string;
	info?: string;
	[key: string]: any;
}

export namespace LiteFlowApi {
	/** 登录接口参数 */
	export interface NodeVO {
		id: string;
		type: string;
		label: string;
		model: string;
		clazz?: string;
		description?: string;
		cmpDataFormCreate?: BuildFormCreate[];
	}

	/** 登录接口返回值 */
	export interface ModelVO {
		label: string;
		model: string;
		description?: string;
	}

	export interface StencilVO {
		nodes: NodeVO[];
		models: ModelVO[];
	}

	export interface LiteFlowNode {
		id: string;
		type: string;
		clazz?: string;
	}
}

export const getJsonToEl = (json: any) => {
	return request({
		url: '/admin/rule/buildJsonToEl',
		method: 'post',
		data: json,
	});
};

export const getElToJson = (el: string, nodes: undefined | LiteFlowApi.LiteFlowNode[]) => {
	return request({
		url: '/admin/rule/buildElToJson',
		method: 'post',
		data: { el: el, nodes: nodes },
	});
};
