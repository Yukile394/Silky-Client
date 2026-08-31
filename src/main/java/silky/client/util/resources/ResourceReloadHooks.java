/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.util.resources;

import silky.client.features.module.Modules;
import silky.client.features.gui.clickgui.settings.I18nDuplicateScanner;
import silky.client.features.gui.clickgui.settings.I18nPreflightManager;
import net.minecraft.server.packs.resources.ResourceManager;
import silky.client.features.module.modules.visuals.NameTags;
import silky.client.util.resources.asset.AssetAutoLoader;
import silky.client.render.engine.prewarm.RenderPrewarmManager;
import silky.client.render.engine.visuals.SilkyVisuals;
import silky.client.render.iris.IrisRuntime;
import silky.client.util.logging.DebugLog;
import silky.client.util.media.MediaSessionService;

public enum ResourceReloadHooks {
    ;

    public static void onReload(ResourceManager manager) {
        if (manager == null) return;

        try {
            // Static resource-backed systems are discovered through @AssetLoad.
            AssetAutoLoader.reload(manager);

            I18nDuplicateScanner.scan(manager, "resource reload");
            I18nPreflightManager.preflight("resource reload");

            // Per-instance/dynamic caches keep their explicit owners.
            NameTags tags = Modules.get(NameTags.class);
            if (tags != null) {
                tags.onResourceReload();
            }

            MediaSessionService.get().onResourceReload();
            SilkyVisuals.onResourceReload(manager);

            try {
                IrisRuntime.registerSilkyPipelines();
            } catch (Throwable t) {
                DebugLog.error("[Silky] Iris pipeline registration failed after resource reload", t);
            }

            // Shader compilation is a discovered post-reload asset hook.
            AssetAutoLoader.postReload(manager);

            try {
                RenderPrewarmManager.prewarm("shader reload complete");
            } catch (Throwable t) {
                DebugLog.error("[Silky] Render prewarm failed after resource reload", t);
            }
        } finally {
            RenderResourceReadiness.markReady("shader reload complete");
        }
    }
}
