/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.framegraph.FramePass;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.resource.ResourceHandle;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LevelTargetBundle;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.client.renderer.state.level.SkyRenderState;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.material.FogType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import silky.client.features.module.Modules;
import silky.client.features.module.modules.visuals.BlockHighlight;
import silky.client.features.module.modules.visuals.ReimaginedVisual;
import silky.client.features.module.modules.visuals.WorldTweaks;
import silky.client.mixins.accessors.GameRendererAccessor;
import silky.client.mixins.accessors.LevelRendererAccessor;
import silky.client.render.engine.core.SilkyWorldMatrices;
import silky.client.render.engine.depth.PreTranslucentDepth;
import silky.client.render.engine.depth.WorldSceneDepth;
import silky.client.render.iris.IrisRuntime;
import silky.client.render.sky.CustomSkyboxRenderer;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {


    @Shadow
    @Final
    private LevelTargetBundle targets;

    @WrapOperation(
            method = "addSkyPass(Lcom/mojang/blaze3d/framegraph/FrameGraphBuilder;Lnet/minecraft/client/renderer/state/level/CameraRenderState;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/GameRenderer;mainRenderTarget()Lcom/mojang/blaze3d/pipeline/RenderTarget;"
            )
    )
    private RenderTarget silky$stableSkyRendererTarget(GameRenderer instance, Operation<RenderTarget> original) {
        return ((GameRendererAccessor) instance).silky$getMainRenderTargetRaw();
    }

    @Unique
    private static void silky$dependsOn(FramePass pass, ResourceHandle<RenderTarget> handle) {
        if (pass != null && handle != null) {
            pass.reads(handle);
        }
    }

    @Unique
    @SuppressWarnings("unchecked")
    private static ResourceHandle<RenderTarget> silky$asRenderTargetHandle(ResourceHandle<?> handle) {
        return (ResourceHandle<RenderTarget>) handle;
    }

    @Unique
    private static RenderTarget silky$getFramebuffer(ResourceHandle<RenderTarget> handle) {
        if (handle == null) {
            return null;
        }
        try {
            return handle.get();
        } catch (Throwable ignored) {
            return null;
        }
    }

    @Unique
    private static boolean silky$needsWorldSceneDepthCapture() {
        ReimaginedVisual module = Modules.get(ReimaginedVisual.class);
        return module != null && module.needsWorldSceneDepthCapture();
    }

    @Unique
    private static boolean shouldSkipSky(net.minecraft.client.renderer.state.level.CameraRenderState cameraRenderState) {
        FogType submersion = cameraRenderState.fogType;
        if (submersion == FogType.POWDER_SNOW || submersion == FogType.LAVA) {
            return true;
        }
        return cameraRenderState.entityRenderState != null
                && cameraRenderState.entityRenderState.doesMobEffectBlockSky;
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void silky$captureIrisModelView(
            GraphicsResourceAllocator allocator,
            DeltaTracker tickCounter,
            boolean renderBlockOutline,
            net.minecraft.client.renderer.state.level.CameraRenderState cameraRenderState,
            org.joml.Matrix4fc positionMatrix,
            GpuBufferSlice fogBuffer,
            org.joml.Vector4f fogColor,
            boolean renderSky,
            CallbackInfo ci
    ) {
        if (positionMatrix == null) {
            return;
        }

        if (SilkyWorldMatrices.isValid()) {
            SilkyWorldMatrices.capturePosition(
                    positionMatrix,
                    cameraRenderState != null && cameraRenderState.pos != null
                            ? cameraRenderState.pos
                            : null
            );
            return;
        }

        if (cameraRenderState != null && cameraRenderState.projectionMatrix != null) {
            SilkyWorldMatrices.capture(
                    positionMatrix,
                    cameraRenderState.projectionMatrix,
                    cameraRenderState.projectionMatrix,
                    cameraRenderState.pos
            );
        }
    }

    @Inject(
            method = "submitBlockOutline",
            at = @At("HEAD"),
            cancellable = true
    )
    private void silky$disableVanillaOutline(PoseStack matrices,
                                                 SubmitNodeCollector consumers,
                                                 LevelRenderState renderState,
                                                 CallbackInfo ci) {
        BlockHighlight blockHighlight = Modules.get(BlockHighlight.class);
        if (blockHighlight != null && blockHighlight.isOutlineActive()) {
            ci.cancel();
        }
    }

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/LevelRenderer;addMainPass(Lcom/mojang/blaze3d/framegraph/FrameGraphBuilder;Lnet/minecraft/client/renderer/feature/FeatureRenderDispatcher$PreparedFrame;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lnet/minecraft/client/renderer/state/level/LevelRenderState;Lnet/minecraft/util/profiling/ProfilerFiller;Lnet/minecraft/client/renderer/chunk/ChunkSectionsToRender;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void silky$captureWorldSceneDepth(
            GraphicsResourceAllocator allocator,
            DeltaTracker tickCounter,
            boolean renderBlockOutline,
            net.minecraft.client.renderer.state.level.CameraRenderState cameraRenderState,
            org.joml.Matrix4fc positionMatrix,
            GpuBufferSlice fogBuffer,
            org.joml.Vector4f fogColor,
            boolean renderSky,
            CallbackInfo ci,
            @Local FrameGraphBuilder frameGraphBuilder
    ) {
        if (IrisRuntime.isShaderpackRendererActive()) {
            return;
        }
        if (frameGraphBuilder == null || targets == null || targets.main == null) {
            return;
        }
        if (!silky$needsWorldSceneDepthCapture()) {
            WorldSceneDepth.reset();
            return;
        }

        ResourceHandle<RenderTarget> main = targets.main;
        ResourceHandle<RenderTarget> translucent = targets.translucent;
        ResourceHandle<RenderTarget> itemEntity = targets.itemEntity;
        ResourceHandle<RenderTarget> particles = targets.particles;
        ResourceHandle<RenderTarget> weather = targets.weather;

        FramePass pass = frameGraphBuilder.addPass("silky_scene_depth_capture");
        silky$dependsOn(pass, main);
        silky$dependsOn(pass, translucent);
        silky$dependsOn(pass, itemEntity);
        silky$dependsOn(pass, particles);
        silky$dependsOn(pass, weather);
        pass.disableCulling();
        pass.executes(() -> {
            RenderTarget mainFramebuffer = silky$getFramebuffer(main);
            Minecraft client = Minecraft.getInstance();
            RenderTarget fallbackFramebuffer = client != null ? client.gameRenderer.mainRenderTarget() : null;
            int width = mainFramebuffer != null ? mainFramebuffer.width : fallbackFramebuffer != null ? fallbackFramebuffer.width : 1;
            int height = mainFramebuffer != null ? mainFramebuffer.height : fallbackFramebuffer != null ? fallbackFramebuffer.height : 1;

            WorldSceneDepth.beginFrame(width, height);
            WorldSceneDepth.captureMain(mainFramebuffer);
            WorldSceneDepth.captureTranslucent(silky$getFramebuffer(translucent));
            WorldSceneDepth.captureItemEntity(silky$getFramebuffer(itemEntity));
            WorldSceneDepth.captureParticles(silky$getFramebuffer(particles));
            WorldSceneDepth.captureWeather(silky$getFramebuffer(weather));
        });
    }

    @Inject(method = "addSkyPass", at = @At("HEAD"), cancellable = true)
    private void silky$renderSky(FrameGraphBuilder frameGraphBuilder, net.minecraft.client.renderer.state.level.CameraRenderState cameraRenderState, GpuBufferSlice fog, CallbackInfo ci) {
        if (IrisRuntime.isModLoaded()) return;
        if (shouldSkipSky(cameraRenderState)) return;

        LevelRendererAccessor accessor = (LevelRendererAccessor) this;
        SkyRenderState sky = accessor.silky$getWorldRenderState().skyRenderState;
        if (sky == null || sky.skybox != DimensionType.Skybox.OVERWORLD) return;

        WorldTweaks worldTweaks = Modules.get(WorldTweaks.class);
        if (worldTweaks != null) {
            worldTweaks.applySkyOverrides(sky);
        }

        ReimaginedVisual module = Modules.get(ReimaginedVisual.class);
        if (module == null || !module.isEnabled()) return;

        Minecraft client = Minecraft.getInstance();
        net.minecraft.client.Camera camera = client != null && client.gameRenderer != null
                ? client.gameRenderer.mainCamera()
                : null;
        if (camera == null) return;

        FramePass pass = frameGraphBuilder.addPass("silky_sky");
        targets.main = pass.readsAndWrites(targets.main);
        pass.executes(() -> CustomSkyboxRenderer.render(camera, fog, sky, accessor.silky$getSkyRendering()));
        ci.cancel();
    }

    @Inject(
            method = "lambda$addMainPass$0",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/chunk/ChunkSectionsToRender;renderGroup(Lnet/minecraft/client/renderer/chunk/ChunkSectionLayerGroup;Lcom/mojang/blaze3d/textures/GpuSampler;)V",
                    ordinal = 1,
                    shift = At.Shift.BEFORE
            ),
            remap = false
    )
    private void silky$capturePreWaterScene(
            GpuBufferSlice fogBuffer,
            LevelRenderState worldRenderState,
            net.minecraft.util.profiling.ProfilerFiller profiler,
            net.minecraft.client.renderer.chunk.ChunkSectionsToRender chunkSections,
            com.mojang.blaze3d.resource.ResourceHandle<?> entityOutlineHandle,
            net.minecraft.client.renderer.feature.FeatureRenderDispatcher.PreparedFrame preparedFrame,
            com.mojang.blaze3d.resource.ResourceHandle<?> translucentFramebufferHandle,
            com.mojang.blaze3d.resource.ResourceHandle<?> mainFramebufferHandle,
            com.mojang.blaze3d.resource.ResourceHandle<?> itemEntityFramebufferHandle,
            com.mojang.blaze3d.resource.ResourceHandle<?> particlesFramebufferHandle,
            CallbackInfo ci
    ) {
        if (IrisRuntime.isShaderpackRendererActive()) {
            return;
        }
        RenderTarget mainFramebuffer = silky$getFramebuffer(silky$asRenderTargetHandle(mainFramebufferHandle));
        if (mainFramebuffer == null) {
            Minecraft mc = Minecraft.getInstance();
            mainFramebuffer = mc != null && mc.gameRenderer != null ? mc.gameRenderer.mainRenderTarget() : null;
        }
        PreTranslucentDepth.captureFrom(mainFramebuffer);
    }
}
