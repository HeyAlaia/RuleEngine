import type { Graph } from '@antv/x6';

import { useCurrNodeStore } from '/@/stores/operation';
import { setActivated } from './node/common';
import { NodeData } from './node/type';

const bindAddNodeEvent = (graph: Graph) => {
  graph.on('node:added', (e: any) => {
    const node = e.node
    const data = node.getData() as NodeData
    if (data.x6Info && data.x6Info.width && data.x6Info.width > 0 && data.x6Info.height && data.x6Info.height > 0) {
      node.setSize(data.x6Info.width, data.x6Info.height)
    }
    setActivated(node, true)
    const { ports } = node.port;
    ports.forEach((port: any) => {
      e.node.portProp(port.id, 'attrs/text/style/display', 'block');
    });
  });
};


const bindNodeMouseenterEvent = (graph: Graph) => {
  graph.on('node:mouseenter', (e: any) => {
    const { ports } = e.node.port;
    ports.forEach((port: any) => {
      e.node.portProp(port.id, 'attrs/circle/style/visibility', 'visible');
      e.node.portProp(port.id, 'attrs/text/style/visibility', 'visible');
    });
  });
};

const bindNodeMouseleaveEvent = (graph: Graph) => {
  graph.on('node:mouseleave', (e: any) => {
    const { ports } = e.node.port;
    ports.forEach((port: any) => {
      e.node.portProp(port.id, 'attrs/circle/style/visibility', 'hidden');
      e.node.portProp(port.id, 'attrs/text/style/visibility', 'hidden');
    });
  });
};

const bindKeyBoardDeleteEvent = (graph: Graph) => {
  graph.bindKey('Backspace', () => {
    const selectCells = graph.getSelectedCells();
    if (selectCells.length === 0) {
      return;
    }
    selectCells.forEach((cell) => {
      if (graph.isNode(cell)) {
        const connectedEdges = graph.getConnectedEdges(cell);
        graph.removeCells(connectedEdges);
      }
      graph.removeCell(cell);
    });
  });
};

const bindKeyBoardUndoEvent = (graph: Graph) => {
  graph.bindKey(['meta+z', 'ctrl+z'], () => {
    if (graph.canUndo()) {
      graph.undo();
    }
    return false;
  });
};

// redo
const bindKeyBoardRedoEvent = (graph: Graph) => {
  graph.bindKey(['meta+shift+z', 'ctrl+shift+z'], () => {
    if (graph.canRedo()) {
      graph.redo();
    }
    return false;
  });
};

const bindSelectNodeEvent = (graph: Graph) => {
  const useCurrNode = useCurrNodeStore();
  graph.on('node:selected', (e: any) => {
    console.log('node:selected', e);
    useCurrNode.setCurrentNode(e.node);
  });
};

// 导出一个名为 bindUnSelectNodeEvent 的常量，它是一个函数，接收一个 Graph 类型的参数
const bindUnselectNodeEvent = (graph: Graph) => {
  graph.on('node:unselected', (_: any) => {
    const useCurrNode = useCurrNodeStore();
    useCurrNode.setCurrentNode(undefined);
  });
};


const clickRemoveNodeEvent = (graph: Graph) => {
	graph.on('cell:contextmenu', ({ e, x, y, node, view }) => {
		e.stopPropagation();
		view.cell.remove();
	});

	graph.on('node:port:contextmenu', ({ e, x, y, node, view }) => {
		e.stopPropagation();
		view.cell.remove();
	});
};



export const initEvent = (graph: Graph) => {
  bindAddNodeEvent(graph);
  bindNodeMouseenterEvent(graph);
  bindNodeMouseleaveEvent(graph);
  bindKeyBoardDeleteEvent(graph);
  bindKeyBoardUndoEvent(graph);
  bindKeyBoardRedoEvent(graph);
  bindSelectNodeEvent(graph);
  bindUnselectNodeEvent(graph);
	clickRemoveNodeEvent(graph);
};
