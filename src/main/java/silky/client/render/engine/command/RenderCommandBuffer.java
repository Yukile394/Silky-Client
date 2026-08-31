/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.render.engine.command;

import silky.client.render.engine.core.RenderFrameContext;

public interface RenderCommandBuffer {
    void clear();

    int size();

    void submit(RenderFrameContext context);
}
