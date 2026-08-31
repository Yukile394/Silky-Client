/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.mixins.accessors;

import com.mojang.blaze3d.textures.GpuSampler;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LevelRenderer.class)
public interface LevelRendererAccessor {

    @Accessor("levelRenderState")
    LevelRenderState silky$getWorldRenderState();

    @Accessor("skyRenderer")
    SkyRenderer silky$getSkyRendering();

    @Accessor("submitNodeStorage")
    SubmitNodeStorage silky$getEntityRenderCommandQueue();

    @Accessor("featureRenderDispatcher")
    FeatureRenderDispatcher silky$getEntityRenderDispatcher();

    @Accessor("renderBuffers")
    RenderBuffers silky$getRenderBuffers();

    @Accessor("renderBuffers")
    void silky$setRenderBuffers(RenderBuffers renderBuffers);

    @Accessor("chunkLayerSampler")
    GpuSampler silky$getTerrainSampler();

    @Accessor("chunkLayerSampler")
    void silky$setTerrainSampler(GpuSampler terrainSampler);

    @Invoker("submitEntities")
    void silky$invokeSubmitEntities(com.mojang.blaze3d.vertex.PoseStack poseStack,
                                        LevelRenderState levelRenderState,
                                        SubmitNodeCollector collector);

}
