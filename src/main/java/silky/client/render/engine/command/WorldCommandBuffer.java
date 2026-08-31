/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.render.engine.command;

import silky.client.render.engine.core.SilkyRenderSystem;
import silky.client.render.engine.core.RenderFrameContext;
import silky.client.render.engine.rhi.RhiDrawCommand;

import java.util.ArrayList;
import java.util.List;

public final class WorldCommandBuffer implements RenderCommandBuffer {
    private final List<RhiDrawCommand> commands = new ArrayList<>();

    public void add(RhiDrawCommand command) {
        if (command != null) commands.add(command);
    }

    @Override
    public void clear() {
        commands.clear();
    }

    @Override
    public int size() {
        return commands.size();
    }

    @Override
    public void submit(RenderFrameContext context) {
        try {
            SilkyRenderSystem.rhi().drawMeshes(commands);
        } finally {
            for (RhiDrawCommand command : commands) {
                if (command != null && command.mesh != null) command.mesh.close();
            }
            clear();
        }
    }
}
