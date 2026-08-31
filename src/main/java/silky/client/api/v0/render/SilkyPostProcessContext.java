/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.api.v0.render;

import com.mojang.blaze3d.textures.GpuTextureView;
import silky.client.render.engine.postprocess.PostProcessPass;
import net.minecraft.client.Minecraft;

public record SilkyPostProcessContext(
        String addonId,
        String passId,
        PostProcessPass.Phase phase,
        GpuTextureView source,
        GpuTextureView destination,
        float tickDelta
) {
    public Minecraft client() {
        return Minecraft.getInstance();
    }
}
