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

package silky.client.mixins;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import silky.client.mixininterface.IRenderPipeline;
import silky.client.render.engine.rhi.clip.ShapeClipRenderPassContract;
import silky.client.render.engine.pipeline.RenderPipelineContract;
import silky.client.render.engine.rhi.pipeline.PipelineDomain;
import silky.client.render.engine.rhi.pipeline.PipelineMetadata;

@Mixin(RenderPipeline.class)
public abstract class RenderPipelineMixin implements IRenderPipeline {
    @Unique
    private boolean silky$lineSmooth;

    @Unique
    private ShapeClipRenderPassContract silky$shapeClipContract = ShapeClipRenderPassContract.NONE;

    @Unique
    private RenderPipelineContract silky$contract = RenderPipelineContract.EXTENDED;

    @Unique
    private PipelineMetadata silky$metadata = PipelineMetadata.builder(PipelineDomain.UNKNOWN).build();

    @Override
    public void silky$setLineSmooth(boolean lineSmooth) {
        this.silky$lineSmooth = lineSmooth;
    }

    @Override
    public boolean silky$getLineSmooth() {
        return silky$lineSmooth;
    }

    @Override
    public void silky$setShapeClipContract(ShapeClipRenderPassContract contract) {
        this.silky$shapeClipContract = contract == null ? ShapeClipRenderPassContract.NONE : contract;
    }

    @Override
    public ShapeClipRenderPassContract silky$getShapeClipContract() {
        return silky$shapeClipContract;
    }

    @Override
    public void silky$setContract(RenderPipelineContract contract) {
        this.silky$contract = contract == null ? RenderPipelineContract.EXTENDED : contract;
    }

    @Override
    public RenderPipelineContract silky$getContract() {
        return silky$contract;
    }

    @Override
    public void silky$setMetadata(PipelineMetadata metadata) {
        this.silky$metadata = metadata != null ? metadata : PipelineMetadata.builder(PipelineDomain.UNKNOWN).build();
    }

    @Override
    public PipelineMetadata silky$getMetadata() {
        return silky$metadata;
    }
}
