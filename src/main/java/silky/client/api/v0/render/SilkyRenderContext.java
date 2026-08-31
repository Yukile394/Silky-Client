/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.api.v0.render;

import com.mojang.blaze3d.vertex.PoseStack;
import silky.client.features.module.HudPhase;
import silky.client.features.module.WorldPhase;
import silky.client.render.engine.renderer.Renderer2D;
import silky.client.render.engine.renderer.Renderer3D;
import silky.client.render.engine.text.TextRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public record SilkyRenderContext(
        String addonId,
        String callbackId,
        SilkyRenderStage stage,
        HudPhase hudPhase,
        WorldPhase worldPhase,
        Renderer2D renderer2D,
        Renderer3D renderer3D,
        Renderer3D depthRenderer3D,
        TextRenderer textRenderer,
        GuiGraphicsExtractor guiContext,
        PoseStack poseStack,
        float tickDelta
) {
    public Minecraft client() {
        return Minecraft.getInstance();
    }
}
