/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.render.engine.text;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import silky.client.render.engine.text.backend.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import silky.client.render.engine.core.SilkyRenderSystem;
import silky.client.render.engine.pipeline.SilkyRenderPipelines;
import silky.client.render.engine.renderer.Renderer2D;
import silky.client.render.engine.rhi.GpuMeshHandle;
import silky.client.render.engine.rhi.RhiDrawCommand;
import silky.client.render.engine.rhi.resource.GlyphAtlasManager;
import silky.client.render.engine.text.backend.*;
import silky.client.render.engine.uniform.MeshBuilder;
import silky.client.render.engine.uniform.impl.MsdfTextUniforms;

import java.util.ArrayList;
import java.util.List;

/**
 * Stage 10 text owner: command buffer, backend router and RHI mesh submission for glyph text.
 */
public enum TextRenderSystem {
    ;
    private static final TextCommandStats STATS = new TextCommandStats();
    private static final TextCommandBuffer COMMANDS = new TextCommandBuffer(STATS);
    private static final TextBackendRouter ROUTER = new TextBackendRouter(STATS);

    static {
        ROUTER.add(new MsdfTextBackend(STATS));
        ROUTER.add(new BitmapAtlasTextBackend(STATS));
        ROUTER.add(new VanillaSodiumTextBackend(STATS));
    }

    public static TextCommandBuffer commands() {
        return COMMANDS;
    }

    public static TextBackendRouter router() {
        return ROUTER;
    }

    public static GlyphAtlasManager glyphAtlases() {
        return SilkyRenderSystem.resources().glyphAtlases();
    }

    public static void beginFrame() {
        COMMANDS.clear();
        STATS.reset();
    }

    public static void record(TextDrawCommand command) {
        COMMANDS.record(command);
    }

    public static void flush() {
        COMMANDS.flush(ROUTER);
    }

    public static TextCommandStatsSnapshot statsSnapshot() {
        return STATS.snapshot();
    }

    public static void noteDirectAdjacentTextBatch(int commands) {
        STATS.directAdjacentTextBatch(commands);
    }

    public static void submitGlyphMesh(String label,
                                       GlyphFont font,
                                       MeshBuilder mesh,
                                       RenderPipeline pipeline,
                                       TextPlacementMode placement) {
        if (font == null || mesh == null || pipeline == null) return;
        if (mesh.isBuilding()) mesh.end();
        if (mesh.getIndicesCount() <= 0) return;

        // When UI rendering is inside a Renderer2D batch, text becomes an ordered batch entry.
        // It may merge only with adjacent compatible text runs; it must never be moved across shapes,
        // items, scissor/marquee boundaries or world/placement-specific text.
        if (placement == null || placement == TextPlacementMode.UI || placement == TextPlacementMode.SCREEN_SPACE) {
            boolean enqueued = Renderer2D.enqueueTextMesh(
                    label, font, mesh, pipeline,
                    placement != null ? placement : TextPlacementMode.UI);
            if (enqueued) {
                return;
            }
        }

        submitGlyphMeshImmediate(label, font, mesh, pipeline, placement);
    }

    public static void submitGlyphMeshImmediate(String label,
                                                GlyphFont font,
                                                MeshBuilder mesh,
                                                RenderPipeline pipeline,
                                                TextPlacementMode placement) {
        List<RhiDrawCommand> commands = new ArrayList<>(1);
        appendGlyphMeshCommand(commands, label, font, mesh, pipeline, placement);
        SilkyRenderSystem.rhi().drawMeshes(commands);
    }

    /** Appends glyph geometry to an existing ordered pass stream without changing painter order. */
    public static void appendGlyphMeshCommand(List<RhiDrawCommand> commands,
                                              String label,
                                              GlyphFont font,
                                              MeshBuilder mesh,
                                              RenderPipeline pipeline,
                                              TextPlacementMode placement) {
        if (commands == null) return;
        if (font == null || mesh == null || pipeline == null) return;
        if (mesh.isBuilding()) mesh.end();
        if (mesh.getIndicesCount() <= 0) return;
        if (!font.isReady()) return;

        AbstractTexture texture = font.getTexture();
        if (texture == null || texture.getTextureView() == null || texture.getSampler() == null) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.gameRenderer.mainRenderTarget() == null) return;

        int vertexBytes = mesh.getVertexBytes();
        int indexBytes = mesh.getIndexBytes();
        GpuMeshHandle handle = null;
        try {
            handle = SilkyRenderSystem.rhi().dynamicMeshes().upload(mesh);
            STATS.meshUpload(vertexBytes, indexBytes);
            STATS.glyphs(Math.max(0, mesh.getVertexCount() / 4));
            if (placement != null && placement.world()) STATS.worldPlacement();
            STATS.backend(font.isMsdf() ? TextBackendPreference.MSDF : TextBackendPreference.BITMAP_ATLAS);

            RhiDrawCommand.Builder command = RhiDrawCommand.builder(label != null ? label : "Silky Text")
                    .pipeline(pipeline)
                    .colorAttachment(mc.gameRenderer.mainRenderTarget().getColorTextureView())
                    .mesh(handle)
                    .sampler("u_Texture", texture.getTextureView(), texture.getSampler());

            if (font.isMsdf()) {
                MsdfTextUniforms.update(font.getPxRange(), font.getAtlasWidth(), font.getAtlasHeight());
                command.uniform("MsdfText", MsdfTextUniforms.get());
            }

            commands.add(command.build());
            handle = null;
        } finally {
            if (handle != null) handle.close();
        }
    }

    public static RenderPipeline uiPipelineFor(GlyphFont font) {
        return font != null && font.isMsdf() ? SilkyRenderPipelines.UI_TEXT_MSDF : SilkyRenderPipelines.UI_TEXT;
    }
}
