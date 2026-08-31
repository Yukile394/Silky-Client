/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.render.engine;

import silky.client.util.resources.asset.AssetAutoLoader;
import silky.client.render.engine.guard.RenderBoundaryAudit;
import silky.client.render.engine.renderer.Renderer2D;
import silky.client.render.engine.visuals.SilkyVisuals;
import silky.client.render.iris.IrisRuntime;
import net.minecraft.client.Minecraft;

public enum SilkyRenderEngineBootstrap {
    ;

    public static void init() {
        RenderBoundaryAudit.runOnce();
        // RHI backend selection must be lazy: onInitializeClient can run before RenderSystem has
        // created the final GpuDevice, which would permanently select the wrong backend on Vulkan.
        Renderer2D.init();
        IrisRuntime.registerSilkyPipelines();
        Minecraft mc = Minecraft.getInstance();
        AssetAutoLoader.initialize(mc != null ? mc.getResourceManager() : null);
        SilkyVisuals.init();
    }

}
