/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.render.engine.visuals;

import com.mojang.blaze3d.textures.GpuTextureView;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;

/**
 * Base contract for the custom world visual graph.
 */
public interface SilkyVisualPass {
    Identifier getId();

    SilkyVisualPhase getPhase();

    default boolean isEnabled() {
        return true;
    }

    default void init() {
    }

    default void onResourceReload(ResourceManager manager) {
    }

    default void prepareFrame(SilkyVisualFrame frame) {
    }

    default boolean render(SilkyVisualFrame frame, GpuTextureView src, GpuTextureView dst) {
        return false;
    }
}
