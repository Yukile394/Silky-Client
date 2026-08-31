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

public enum PostProcessUniforms {
    ;
    public static final int SIZE = new Std140SizeCalculator()
            .putFloat()
            .putFloat()
            .putFloat()
            .putFloat()
            .get();

    private static final Data DATA = new Data();
    private static final String UNIFORM_NAME = "Silky - PostProcess UBO";
    private static final int EXPECTED_WRITES_PER_FRAME = 16;

    public static void update(float strength, float desat, float contrast, float padding) {
        DATA.strength = strength;
        DATA.desat = desat;
        DATA.contrast = contrast;
        DATA.padding = padding;
        SilkyRenderSystem.uniforms().write(UNIFORM_NAME, SIZE, EXPECTED_WRITES_PER_FRAME, DATA);
    }

    public static GpuBufferSlice get() {
        return SilkyRenderSystem.uniforms().current(UNIFORM_NAME);
    }

    private static final class Data implements SilkyUniformAllocator.UniformWriter {
        private float strength;
        private float desat;
        private float contrast;
        private float padding;

        @Override
        public void write(java.nio.ByteBuffer buffer) {
            Std140Builder.intoBuffer(buffer)
                    .putFloat(strength)
                    .putFloat(desat)
                    .putFloat(contrast)
                    .putFloat(padding);
        }

        @Override
        public boolean equals(Object o) {
            return false;
        }
    }

}
