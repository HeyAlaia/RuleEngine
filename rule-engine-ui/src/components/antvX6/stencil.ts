import type { EventArgs } from '@antv/x6';

import { Stencil } from '@antv/x6-plugin-stencil';

export class PippinStencil extends Stencil {
  protected override onDragStart(args: EventArgs['node:mousedown']): void {
    super.onDragStart(args);
  }
}
