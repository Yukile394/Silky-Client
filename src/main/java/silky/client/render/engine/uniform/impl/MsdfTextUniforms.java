/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.render.engine.uniform.impl;

import silky.client.render.engine.core.SilkyRenderSystem;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.buffers.Std140Builder;
import com.mojang.blaze3d.buffers.Std140SizeCalculator;
import silky.client.render.engine.rhi.uniform.SilkyUniformAllocator;

/**
 * Std140 UBO for MSDF text rendering.
 * <p>
 * Layout (std140):
 * vec4 data; // x = pxRange, y = atlasWidth, z = atlasHeight, w = unused
 */
public enum MsdfTextUniforms {
    ;
    public static final int SIZE = new Std140SizeCalculator()
            .putVec4()
            .get();

    private static final Data DATA = new Data();
    private static final String UNIFORM_NAME = "Silky - MSDF Text UBO";
    private static final int EXPECTED_WRITES_PER_FRAME = 16;
    private static long lastFrameId = Long.MIN_VALUE;
    private static float lastPxRange = Float.NaN;
    private static float lastAtlasWidth = Float.NaN;
    private static float lastAtlasHeight = Float.NaN;

    public static void update(float pxRange, int atlasWidth, int atlasHeight) {
        float safeW = atlasWidth <= 0 ? 1f : atlasWidth;
        float safeH = atlasHeight <= 0 ? 1f : atlasHeight;

        SilkyUniformAllocator allocator = SilkyRenderSystem.uniforms();
        long frameId = allocator.frameId();
        if (allocator.hasCurrent(UNIFORM_NAME)
                && lastFrameId == frameId
                && Float.compare(lastPxRange, pxRange) == 0
                && Float.compare(lastAtlasWidth, safeW) == 0
                && Float.compare(lastAtlasHeight, safeH) == 0) {
            return;
        }

        DATA.values[0] = pxRange;
        DATA.values[1] = safeW;
        DATA.values[2] = safeH;
        DATA.values[3] = 0f;

        allocator.write(UNIFORM_NAME, SIZE, EXPECTED_WRITES_PER_FRAME, DATA);
        lastFrameId = frameId;
        lastPxRange = pxRange;
        lastAtlasWidth = safeW;
        lastAtlasHeight = safeH;
    }

    public static GpuBufferSlice get() {
        return SilkyRenderSystem.uniforms().current(UNIFORM_NAME);
    }

    private static final class Data implements SilkyUniformAllocator.UniformWriter {
        private final float[] values = new float[4];

        @Override
        public void write(java.nio.ByteBuffer buffer) {
            Std140Builder.intoBuffer(buffer)
                    .putFloat(values[0])
                    .putFloat(values[1])
                    .putFloat(values[2])
                    .putFloat(values[3]);
        }
    }

}
