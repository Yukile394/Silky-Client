/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.api.v0.render;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import silky.client.render.engine.core.SilkyRenderSystem;

import java.nio.ByteBuffer;

@SuppressWarnings("unused")
public enum SilkyUniforms {
    ;

    public static GpuBufferSlice write(String name,
                                       int std140Size,
                                       int expectedWritesPerFrame,
                                       UniformWriter writer) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Uniform name cannot be blank");
        }
        if (std140Size <= 0) {
            throw new IllegalArgumentException("Uniform std140 size must be positive");
        }
        if (writer == null) {
            throw new IllegalArgumentException("Uniform writer cannot be null");
        }
        return SilkyRenderSystem.uniforms().write(name, std140Size, expectedWritesPerFrame, writer::write);
    }

    public static GpuBufferSlice writeCached(String name,
                                             int std140Size,
                                             int expectedWritesPerFrame,
                                             int updateEveryFrames,
                                             boolean force,
                                             UniformWriter writer) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Uniform name cannot be blank");
        }
        if (std140Size <= 0) {
            throw new IllegalArgumentException("Uniform std140 size must be positive");
        }
        if (writer == null) {
            throw new IllegalArgumentException("Uniform writer cannot be null");
        }
        return SilkyRenderSystem.uniforms().writeCached(
                name,
                std140Size,
                expectedWritesPerFrame,
                updateEveryFrames,
                force,
                writer::write
        );
    }

    public static GpuBufferSlice current(String name) {
        if (name == null || name.isBlank()) return null;
        return SilkyRenderSystem.uniforms().current(name);
    }

    public static void invalidate(String name) {
        if (name == null || name.isBlank()) return;
        SilkyRenderSystem.uniforms().invalidate(name);
    }

    @FunctionalInterface
    public interface UniformWriter {
        void write(ByteBuffer buffer);
    }
}
