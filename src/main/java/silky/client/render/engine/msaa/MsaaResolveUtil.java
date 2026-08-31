/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.render.engine.msaa;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import silky.client.render.engine.core.SilkyRenderSystem;

@Deprecated
public enum MsaaResolveUtil {
    ;

    public static boolean resolve(RenderTarget src, RenderTarget dst, boolean color, boolean depth) {
        RenderSystem.assertOnRenderThread();
        return SilkyRenderSystem.rhi().msaa().resolve(src, dst, color, depth);
    }
}
