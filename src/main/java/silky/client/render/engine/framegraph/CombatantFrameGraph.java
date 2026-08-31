/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.render.engine.framegraph;

import silky.client.render.engine.core.SilkyRenderSystem;
import silky.client.render.engine.core.RenderFrameContext;
import silky.client.render.engine.core.RenderPhase;
import silky.client.render.engine.core.RenderPhaseScope;

import java.util.function.Consumer;


public final class SilkyFrameGraph {
    private final PassScheduler scheduler = new PassScheduler();

    public void add(RenderPhase phase, Consumer<RenderFrameContext> handler) {
        add(phase, phase == null ? "unnamed" : phase.name().toLowerCase(), handler);
    }

    public void add(RenderPhase phase, String label, Consumer<RenderFrameContext> handler) {
        if (handler == null) return;
        scheduler.add(new RenderPassNode(phase, label, handler));
    }

    public void execute(RenderPhase phase, RenderFrameContext baseContext) {
        String label = "framegraph:" + (phase == null ? "none" : phase.name().toLowerCase());
        try (RenderPhaseScope ignored = SilkyRenderSystem.phase(phase, label)) {
            RenderFrameContext ctx = SilkyRenderSystem.currentContext();
            if (ctx == null && baseContext != null) ctx = baseContext.withPhase(phase);
            if (ctx == null) return;
            for (RenderPassNode node : scheduler.nodes(phase)) {
                try (RenderPhaseScope nodeScope = SilkyRenderSystem.phase(phase, "pass:" + node.label())) {
                    node.execute(SilkyRenderSystem.currentContext());
                }
            }
        }
    }

    public void clear() {
        scheduler.clear();
    }
}
