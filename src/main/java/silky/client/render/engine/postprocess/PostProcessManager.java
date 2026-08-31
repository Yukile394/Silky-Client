/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.render.engine.postprocess;

import com.mojang.blaze3d.ProjectionType;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuSampler;
import com.mojang.blaze3d.textures.GpuTextureView;
import net.minecraft.client.Minecraft;
import org.joml.Matrix4f;
import silky.client.render.engine.RenderState;
import silky.client.render.engine.core.SilkyRenderSystem;
import silky.client.render.engine.pipeline.SilkyRenderPipelines;
import silky.client.render.engine.postprocess.graph.PostProcessGraph;
import silky.client.render.engine.postprocess.graph.PostProcessGraphPass;
import silky.client.render.engine.profiler.RenderCostProfiler;
import silky.client.render.engine.renderer.FullScreenRenderer;
import silky.client.render.engine.renderer.MeshRenderer;
import silky.client.render.engine.rhi.FullscreenDrawCommand;
import silky.client.runtime.RuntimeGate;

import java.util.OptionalDouble;
import java.util.function.Predicate;

public enum PostProcessManager {
    ;
    private static final PostProcessGraph GRAPH = new PostProcessGraph();
    private static GpuSampler sampler;

    public static void register(PostProcessPass pass) {
        GRAPH.addLegacy(pass);
    }

    public static PostProcessGraph graph() {
        return GRAPH;
    }

    public static void renderAll(PostProcessPass.Phase phase, float tickDelta) {
        renderSelected(phase, tickDelta, pass -> true);
    }

    /**
     * Runs a deliberately isolated subset of a phase. Preview scenes use this to reuse a feature's
     * real compositor without pulling unrelated world post effects into the preview framebuffer.
     */
    public static void renderSelected(PostProcessPass.Phase phase,
                                      float tickDelta,
                                      Predicate<? super PostProcessGraphPass> selector) {
        if (!RuntimeGate.canRunRender()) return;
        if (selector == null) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.gameRenderer.mainRenderTarget() == null) return;

        FullScreenRenderer.ensureInit();
        ensureSampler();

        var mv = RenderSystem.getModelViewStack();
        Matrix4f previousProjection = MeshRenderer.projection();
        GpuBufferSlice previousProjectionBuffer = RenderSystem.getProjectionMatrixBuffer();
        ProjectionType previousProjectionType = RenderSystem.getProjectionType();
        boolean previousRendering3D = RenderState.rendering3D;
        mv.pushMatrix();
        mv.identity();
        MeshRenderer.setProjection(new Matrix4f().identity());
        RenderState.rendering3D = false;

        try {
            GRAPH.execute(
                    phase,
                    tickDelta,
                     SilkyRenderSystem.ensureFrameContext(),
                     SilkyRenderSystem.rhi(),
                     PostProcessManager::copy,
                     selector
             );
        } finally {
            MeshRenderer.setProjection(previousProjection);
            if (previousProjectionBuffer != null && previousProjectionType != null) {
                RenderSystem.setProjectionMatrix(previousProjectionBuffer, previousProjectionType);
            }
            RenderState.rendering3D = previousRendering3D;
            mv.popMatrix();
        }
    }

    private static void ensureSampler() {
        if (sampler != null) return;
        sampler = RenderSystem.getDevice().createSampler(
                AddressMode.CLAMP_TO_EDGE,
                AddressMode.CLAMP_TO_EDGE,
                FilterMode.LINEAR,
                FilterMode.LINEAR,
                1,
                OptionalDouble.empty()
        );
    }

    public static GpuSampler getSampler() {
        ensureSampler();
        return sampler;
    }

    public static void copy(GpuTextureView src, GpuTextureView dst) {
        try (RenderCostProfiler.Scope ignored = RenderCostProfiler.postPass("copy")) {
            if (src == null || dst == null || src == dst) return;
            if (SilkyRenderSystem.rhi().textureBlitter().copyFast(src, dst)) {
                return;
            }

            ensureSampler();
            SilkyRenderSystem.rhi().drawFullscreen(
                    FullscreenDrawCommand.builder("Silky PostProcess Copy")
                            .colorAttachment(dst)
                            .pipeline(SilkyRenderPipelines.POSTPROCESS_COPY)
                            .sampler("u_Texture", src, sampler)
                            .build()
            );
            SilkyRenderSystem.rhi().stats().textureShaderCopy();
        }
    }

    public static void shutdownForRuntime() {
        GRAPH.close();
        sampler = null;
    }
}
