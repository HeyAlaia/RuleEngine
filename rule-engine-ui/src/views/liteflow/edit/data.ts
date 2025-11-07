import { LiteFlowApi } from "/@/api/liteflow/operation";

export const initData = {
    nodes: [
        {
            id: 'pippin-not-bool',
            type: 'booleanNotCondition',
            label: 'NOT 非',
            model: 'init',
            description: '流程节点，不支持创建初始节点',
        },
        {
            id: 'pippin-if',
            type: 'if',
            label: 'IF 条件编排',
            model: 'init',
            description: '流程节点，不支持创建初始节点',
        },
        {
            id: 'pippin-catch',
            type: 'catch',
            label: 'CATCH 异常编排',
            model: 'init',
            description: '流程节点，不支持创建初始节点',
        },
        {
            id: 'pippin-pre',
            type: 'pre',
            label: 'PRE 前置编排',
            model: 'init',
            description: '流程节点，不支持创建初始节点',
        },
        {
            id: 'pippin-finally',
            type: 'finally',
            label: 'FINALLY 后置编排',
            model: 'init',
            description: '流程节点，不支持创建初始节点',
        },
        {
            id: 'pippin-when',
            type: 'when',
            label: 'WHEN 并行编排',
            model: 'init',
            description: '流程节点，不支持创建初始节点',
        },
        {
            id: 'pippin-while',
            type: 'while',
            label: 'WHILE 循环编排',
            model: 'init',
            description: '流程节点，不支持创建初始节点',
        },
        {
            id: 'pippin-tag',
            type: 'tag',
            label: 'TAG 标签',
            model: 'init',
            description: '流程节点，不支持创建初始节点',
            cmpDataFormCreate: [
                {
                    props: {},
                    title: 'tag标签',
                    field: 'tag',
                    type: 'input',
                    info: '可连接tag 连接点，当tag有后续连接时，会讲tag标签数据加在后续表达式中',
                },
            ],
        },
    ],
    models: [
        {
            label: '流程内部节点',
            model: 'init',
            description: 'string',
        },
    ],
} as LiteFlowApi.StencilVO;

export const defaultCustomerData = {
    nodes: [
        {
            id: 'boolNode',
            type: 'boolean',
            label: '测试bool',
            model: 'default',
            description: '测试节点',
        },
        {
            id: 'switchNode',
            type: 'switch',
            label: '测试switch',
            model: 'default',
            description: '测试节点',
        },
        {
            id: 'iteratorNode',
            type: 'iterator',
            label: '测试iterator',
            model: 'default',
            description: '测试节点',
        },
        {
            id: 'forNode',
            type: 'for',
            label: '测试for',
            model: 'default',
            description: '测试节点',
        },
        {
            id: 'commonNode',
            type: 'common',
            label: '测试common',
            model: 'default',
            description: '测试节点',
        },
    ],
    models: [
        {
            label: '默认节点',
            model: 'default',
            description: '当未进行初始化时使用默认节点',
        },
    ],
}
