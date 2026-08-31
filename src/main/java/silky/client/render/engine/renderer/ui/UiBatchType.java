/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.render.engine.renderer.ui;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import silky.client.render.engine.pipeline.SilkyRenderPipelines;

public enum UiBatchType {
    TEXTURED(SilkyRenderPipelines.UI_TEXTURED_FAST, true, true),
    SVG_MSDF(SilkyRenderPipelines.UI_SVG_MSDF_FAST, true, true),
    LINES(SilkyRenderPipelines.UI_COLORED_LINES_FAST, true, false),
    ORBIZ_RING(SilkyRenderPipelines.UI_ORBIZ_RING_BATCH, true, false),
    ROUNDED_FILL_SMOKE(SilkyRenderPipelines.UI_ROUNDED_FILL_SMOKE_BATCH, true, false),
    MODULE_CATEGORY_SURFACE(SilkyRenderPipelines.UI_MODULE_CATEGORY_SURFACE_BATCH, true, false),
    MAIN_MENU_HONEYCOMB(SilkyRenderPipelines.UI_MAIN_MENU_HONEYCOMB_BATCH, true, true),
    ROUNDED_STROKE_ANGULAR(SilkyRenderPipelines.UI_ROUNDED_STROKE_ANGULAR_BATCH, true, false),
    SHAPE(SilkyRenderPipelines.UI_SHAPE_BATCH, true, false),
    PRIMITIVE(SilkyRenderPipelines.UI_PRIMITIVE_BATCH, true, false),
    GLOW(SilkyRenderPipelines.UI_GLOW_BATCH, true, false),
    TEXTURED_SHAPE(SilkyRenderPipelines.UI_TEXTURED_SHAPE_BATCH, true, true),
    BLUR(SilkyRenderPipelines.UI_BLUR_BATCH, true, true),
    BLUR_CORNERS(SilkyRenderPipelines.UI_BLUR_BATCH_CORNERS, true, true),
    LIQUID_GLASS(SilkyRenderPipelines.UI_LIQUID_GLASS_BATCH, true, true);

    public final RenderPipeline pipeline;
    public final boolean needsUiBatch;
    public final boolean usesSampler;

    UiBatchType(RenderPipeline pipeline, boolean needsUiBatch, boolean usesSampler) {
        this.pipeline = pipeline;
        this.needsUiBatch = needsUiBatch;
        this.usesSampler = usesSampler;
    }

    /** Materials that sample both the captured clean scene and its shared Kawase blur. */
    public boolean usesPreparedGlass() {
        return this == LIQUID_GLASS || this == MAIN_MENU_HONEYCOMB;
    }
}
