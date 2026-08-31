/*
 * This file is part of the Silky Client distribution.
 * Silky modifications copyright (c) 2026 pivosos2007.
 *
 * Portions of this file are based on, adapted from, or implemented
 * with reference to Meteor Client
 * (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 *
 * Licensed under the GNU General Public License v3.0.
 * See THIRD_PARTY_NOTICES.md for details.
 */

package silky.client.mixininterface;

import silky.client.render.engine.rhi.clip.ShapeClipRenderPassContract;
import silky.client.render.engine.pipeline.RenderPipelineContract;
import silky.client.render.engine.rhi.pipeline.PipelineMetadata;

public interface IRenderPipeline {
    void silky$setLineSmooth(boolean lineSmooth);

    boolean silky$getLineSmooth();

    void silky$setShapeClipContract(ShapeClipRenderPassContract contract);

    ShapeClipRenderPassContract silky$getShapeClipContract();

    void silky$setContract(RenderPipelineContract contract);

    RenderPipelineContract silky$getContract();

    void silky$setMetadata(PipelineMetadata metadata);

    PipelineMetadata silky$getMetadata();
}
