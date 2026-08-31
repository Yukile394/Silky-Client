/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.render.engine.renderer.ui;

import silky.client.render.engine.command.UiCommandBuffer;

public final class UiPassCompiler {
    private final UiBatcher batcher = new UiBatcher();

    public UiBatchPlan compile(UiCommandBuffer commands) {
        return batcher.compile(commands);
    }
}
