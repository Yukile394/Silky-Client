/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.render.engine.postprocess.graph;

import silky.client.render.engine.core.RenderFrameContext;
import silky.client.render.engine.postprocess.PostProcessPass;
import silky.client.render.engine.rhi.SilkyRhi;

import java.util.Set;

public interface PostProcessGraphPass {
    String id();

    default String getId() {
        return id();
    }

    int priority();

    PostProcessPass.Phase phase();

    Set<PostProcessResource> reads();

    Set<PostProcessResource> writes();

    PostProcessResolution resolution();

    boolean enabled(RenderFrameContext context);

    /**
     * Returns true when the pass wrote its destination and the graph should advance ping-pong state.
     */
    boolean execute(RenderFrameContext context, SilkyRhi rhi, PostProcessGraphResources resources);
}
