/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.render.engine.text;

import silky.client.render.engine.core.SilkyRenderSystem;
import silky.client.render.engine.core.RenderFrameContext;
import silky.client.render.engine.rhi.SilkyRhi;
import silky.client.render.engine.text.backend.TextBackendRouter;
import silky.client.render.engine.text.backend.TextDrawCommand;

import java.util.ArrayList;
import java.util.List;

/**
 * Frame-local text command buffer. It is intentionally independent from Renderer2D.
 */
public final class TextCommandBuffer {
    private final List<TextDrawCommand> commands = new ArrayList<>();
    private final TextCommandStats stats;

    public TextCommandBuffer(TextCommandStats stats) {
        this.stats = stats;
    }

    public void record(TextDrawCommand command) {
        if (command == null || command.text() == null || command.text().isEmpty()) return;
        commands.add(command);
        stats.recorded();
        if (command.effect() != null && command.effect().enabled()) stats.effect();
        if (command.clip() != null && command.clip().enabled()) stats.clipped();
    }

    public int size() {
        return commands.size();
    }

    public void flush(TextBackendRouter router) {
        if (commands.isEmpty()) return;
        RenderFrameContext context = SilkyRenderSystem.ensureFrameContext();
        SilkyRhi rhi = SilkyRenderSystem.rhi();
        for (int i = 0; i < commands.size(); ) {
            int consumed = Math.max(1, router.drawAdjacent(commands, i, context, rhi));
            for (int j = 0; j < consumed && i + j < commands.size(); j++) {
                stats.submitted();
            }
            i += consumed;
        }
        commands.clear();
    }

    public void clear() {
        commands.clear();
    }
}
