/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.render.engine.rhi;

import silky.client.render.engine.rhi.blit.TextureBlitter;
import silky.client.render.engine.rhi.clip.ShapeClipBackend;
import silky.client.render.engine.rhi.fullscreen.FullscreenBackend;
import silky.client.render.engine.rhi.msaa.MsaaControl;
import silky.client.render.engine.rhi.pipeline.RenderPipelineRegistry;
import silky.client.render.engine.rhi.resource.RenderResourceManager;
import silky.client.render.engine.rhi.state.PipelineStateBackend;
import silky.client.render.engine.rhi.upload.DynamicMeshBackend;

import java.util.List;

public interface SilkyRhi extends AutoCloseable {
    DynamicMeshBackend dynamicMeshes();

    FullscreenBackend fullscreen();

    TextureBlitter textureBlitter();

    MsaaControl msaa();

    ShapeClipBackend shapeClip();

    PipelineStateBackend pipelineState();

    RhiStats stats();

    RenderPipelineRegistry pipelines();

    RenderResourceManager resources();

    void beginFrame(long frameId);

    void endRenderSubmission();

    void framePresented();

    default void drawMesh(RhiDrawCommand command) {
        if (command != null) drawMeshes(List.of(command));
    }

    /**
     * Executes an ordered draw stream. Backends may keep one render pass open across adjacent
     * commands when their attachments are compatible. Command order is never changed.
     */
    void drawMeshes(List<RhiDrawCommand> commands);

    void drawFullscreen(FullscreenDrawCommand command);

    @Override
    void close();
}
