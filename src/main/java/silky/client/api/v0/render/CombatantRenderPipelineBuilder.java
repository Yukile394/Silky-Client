/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.api.v0.render;

import com.mojang.blaze3d.PrimitiveTopology;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.vertex.VertexFormat;
import silky.client.render.engine.pipeline.SilkyRenderPipelines;
import silky.client.render.engine.pipeline.DepthTestFunction;
import silky.client.render.engine.pipeline.ExtendedRenderPipelineBuilder;
import silky.client.render.engine.pipeline.RenderPipelineContract;
import silky.client.render.engine.rhi.clip.ShapeClipRenderPassContract;
import silky.client.render.engine.rhi.pipeline.PipelineDomain;
import silky.client.render.engine.rhi.pipeline.TransformPolicy;
import net.minecraft.resources.Identifier;

@SuppressWarnings("unused")
public final class SilkyRenderPipelineBuilder {
    private final ExtendedRenderPipelineBuilder delegate;

    private SilkyRenderPipelineBuilder(RenderPipeline.Snippet... snippets) {
        this.delegate = new ExtendedRenderPipelineBuilder(snippets);
    }

    public static SilkyRenderPipelineBuilder create(RenderPipeline.Snippet... snippets) {
        return new SilkyRenderPipelineBuilder(snippets);
    }

    public static RenderPipeline.Snippet meshUniforms() {
        return SilkyRenderPipelines.meshUniforms();
    }

    public SilkyRenderPipelineBuilder location(Identifier id) {
        delegate.withLocation(id);
        return this;
    }

    public SilkyRenderPipelineBuilder vertexFormat(VertexFormat format, PrimitiveTopology mode) {
        delegate.withVertexFormat(format, mode);
        return this;
    }

    public SilkyRenderPipelineBuilder vertexShader(Identifier id) {
        delegate.withVertexShader(id);
        return this;
    }

    public SilkyRenderPipelineBuilder fragmentShader(Identifier id) {
        delegate.withFragmentShader(id);
        return this;
    }

    public SilkyRenderPipelineBuilder depthTest(DepthTestFunction function) {
        delegate.withDepthTestFunction(function);
        return this;
    }

    public SilkyRenderPipelineBuilder depthWrite(boolean write) {
        delegate.withDepthWrite(write);
        return this;
    }

    public SilkyRenderPipelineBuilder blend(BlendFunction blend) {
        delegate.withBlend(blend);
        return this;
    }

    public SilkyRenderPipelineBuilder cull(boolean cull) {
        delegate.withCull(cull);
        return this;
    }

    public SilkyRenderPipelineBuilder sampler(String name) {
        delegate.withSampler(name);
        return this;
    }

    public SilkyRenderPipelineBuilder uniform(String name, UniformType type) {
        delegate.withUniform(name, type);
        return this;
    }

    public SilkyRenderPipelineBuilder contract(RenderPipelineContract contract) {
        delegate.withContract(contract);
        return this;
    }

    public SilkyRenderPipelineBuilder domain(PipelineDomain domain) {
        delegate.withDomain(domain);
        return this;
    }

    public SilkyRenderPipelineBuilder transformPolicy(TransformPolicy policy) {
        delegate.withTransformPolicy(policy);
        return this;
    }

    public SilkyRenderPipelineBuilder shapeFamily(String family) {
        delegate.withShapeFamily(family);
        return this;
    }

    public SilkyRenderPipelineBuilder lineSmooth() {
        delegate.withLineSmooth();
        return this;
    }

    public SilkyRenderPipelineBuilder shapeClipSupport() {
        delegate.withShapeClipSupport();
        return this;
    }

    public SilkyRenderPipelineBuilder shapeClipContract(ShapeClipRenderPassContract contract) {
        delegate.withShapeClipContract(contract);
        return this;
    }

    public RenderPipeline build() {
        return delegate.build();
    }

    public RenderPipeline buildAndRegister() {
        return SilkyRenderPipelines.registerAddonPipeline(build());
    }
}
