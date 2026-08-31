/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.vertex.PoseStack;
import silky.client.features.gui.preview.VisualPreviewRuntime;
import silky.client.features.module.modules.visuals.*;
import silky.client.render.engine.postprocess.PostProcessPass;
import silky.client.util.screen.ClientScreen;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.ProjectionMatrixBuffer;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.feature.FeatureRenderDispatcher;
import net.minecraft.client.renderer.state.GameRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import silky.client.features.gui.hud.nondraggable.impl.BetterButtons;
import silky.client.addon.AddonRenderPipelineManager;
import silky.client.api.v0.render.SilkyRenderStage;
import silky.client.features.module.ModuleManager;
import silky.client.features.module.Modules;
import silky.client.features.module.WorldPhase;
import silky.client.features.module.modules.visuals.*;
import silky.client.mixins.accessors.GameRendererAccessor;
import silky.client.mixins.accessors.LocalPlayerAccessor;
import silky.client.render.engine.RenderState;
import silky.client.render.engine.core.SilkyRenderSystem;
import silky.client.render.engine.core.SilkyWorldMatrices;
import silky.client.render.engine.core.RenderPhase;
import silky.client.render.engine.core.RenderPhaseScope;
import silky.client.render.engine.depth.WorldSceneDepth;
// import silky.client.render.engine.debug.RenderThread2DDebugRenderer; // disabled: debug probes are manual-only
import silky.client.render.engine.msaa.MsaaWorldTarget;
import silky.client.config.MainConfig;
import silky.client.render.engine.pipeline.SilkyRenderPipelines;
import silky.client.render.engine.postprocess.PostProcessManager;
import silky.client.render.engine.postprocess.MenuBackgroundRenderer;
import silky.client.render.engine.profiler.ProfilerPhase;
import silky.client.render.engine.profiler.RenderProfiler3D;
import silky.client.render.engine.profiler.TracyGpuProfiler;
import silky.client.render.engine.renderer.MeshRenderer;
import silky.client.render.engine.renderer.Renderer2D;
import silky.client.render.engine.renderer.Renderer3D;
import silky.client.render.engine.text.TextRenderer;
import silky.client.render.engine.visuals.SilkyVisuals;
import silky.client.render.iris.IrisSilkyFrameHooks;
import silky.client.render.iris.IrisFinalizedSceneRenderer;
import silky.client.render.iris.IrisCompatibilityGuards;
import silky.client.render.iris.IrisRuntime;
import silky.client.util.logging.DebugLog;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin implements IrisFinalizedSceneRenderer {

    @Unique
    private static final boolean SILKY_DEBUG_PRIMITIVE = false;
    @Unique
    private static final boolean SILKY_DEBUG_CLIP_SPACE = false;
    @Unique
    private static final long SILKY_DEBUG_INTERVAL_MS = 2000L;
    @Unique
    private static long SILKY_DEBUG_LAST_MS;
    @Unique
    private final PoseStack silky$matrices = new PoseStack();
    @Unique
    private final PoseStack silky$postMatrices = new PoseStack();
    @Shadow
    @Final
    private Minecraft minecraft;
    @Shadow
    @Final
    private Camera mainCamera;
    @Shadow
    @Final
    private GameRenderState gameRenderState;
    @Shadow
    private float spinningEffectTime;
    @Shadow
    private float spinningEffectSpeed;
    @Unique
    private Renderer3D silky$renderer;
    @Unique
    private Renderer3D silky$depthRenderer;
    @Unique
    private Renderer3D silky$postRenderer;
    @Unique
    private Renderer3D silky$postDepthRenderer;

    @Unique
    private static void silky$drawDebugCube(Renderer3D rdr,
                                                double x1, double y1, double z1,
                                                double x2, double y2, double z2) {
        rdr.line(x1, y1, z1, x2, y1, z1, 255, 0, 0, 255);
        rdr.line(x1, y2, z1, x2, y2, z1, 255, 0, 0, 255);
        rdr.line(x1, y1, z2, x2, y1, z2, 255, 0, 0, 255);
        rdr.line(x1, y2, z2, x2, y2, z2, 255, 0, 0, 255);

        rdr.line(x1, y1, z1, x1, y2, z1, 0, 255, 0, 255);
        rdr.line(x2, y1, z1, x2, y2, z1, 0, 255, 0, 255);
        rdr.line(x1, y1, z2, x1, y2, z2, 0, 255, 0, 255);
        rdr.line(x2, y1, z2, x2, y2, z2, 0, 255, 0, 255);

        rdr.line(x1, y1, z1, x1, y1, z2, 0, 0, 255, 255);
        rdr.line(x2, y1, z1, x2, y1, z2, 0, 0, 255, 255);
        rdr.line(x1, y2, z1, x1, y2, z2, 0, 0, 255, 255);
        rdr.line(x2, y2, z1, x2, y2, z2, 0, 0, 255, 255);
    }

    @Unique
    private static boolean isWorldTranslucent(FogType type) {
        return type == FogType.WATER
                || type == FogType.LAVA
                || type == FogType.POWDER_SNOW;
    }

    @Shadow
    protected abstract void bobView(CameraRenderState cameraRenderState, PoseStack matrices);

    @Shadow
    protected abstract void bobHurt(CameraRenderState cameraRenderState, PoseStack matrices);

    @Unique
    private static boolean silky$needsResolvedMainDepth() {
        ReimaginedVisual module = Modules.get(ReimaginedVisual.class);
        if (module == null) return false;
        if (module.needsResolvedMainDepthCapture()) return true;
        return MainConfig.get().getMsaa3dSamples() > 1 && module.isActive();
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void silky$skipTickWithoutPlayer(CallbackInfo ci) {
        if (minecraft != null && minecraft.level != null && minecraft.player == null) {
            ci.cancel();
        }
    }

    @Inject(method = "extract(Lnet/minecraft/client/DeltaTracker;Z)V", at = @At("HEAD"))
    private void silky$beginDeferred2DExtract(DeltaTracker tickCounter, boolean tick, CallbackInfo ci) {
        Renderer2D.beginDeferredExtractFrame();
    }

    @Inject(method = "extract(Lnet/minecraft/client/DeltaTracker;Z)V", at = @At("TAIL"))
    private void silky$endDeferred2DExtract(DeltaTracker tickCounter, boolean tick, CallbackInfo ci) {
        Renderer2D.endDeferredExtractFrame();
    }

    @Redirect(
            method = "renderItemInHand(Lnet/minecraft/client/renderer/state/level/CameraRenderState;FLorg/joml/Matrix4fc;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/GameRenderer;bobView(Lnet/minecraft/client/renderer/state/level/CameraRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;)V"
            )
    )
    private void silky$viewModel$replaceHandBobbing(GameRenderer instance,
                                                        CameraRenderState cameraRenderState,
                                                        PoseStack matrices,
                                                        CameraRenderState originalCameraRenderState,
                                                        float tickDelta,
                                                        Matrix4fc positionMatrix) {
        ViewModel viewModel = Modules.get(ViewModel.class);
        if (viewModel != null && viewModel.smoothRunLoweringEnabled()) {
            if (minecraft != null && minecraft.getCameraEntity() instanceof AbstractClientPlayer player) {
                float movement = player.avatarState().getInterpolatedBob(tickDelta);
                float yOffset = -Mth.clamp(movement, 0.0f, 1.0f) * viewModel.getSmoothRunLoweringAmount();
                if (Math.abs(yOffset) > 0.0001f) {
                    matrices.translate(0.0f, yOffset, 0.0f);
                }
            }
            return;
        }

        ((GameRendererAccessor) instance).invokeBobView(cameraRenderState, matrices);
    }

    @Inject(method = "extractCamera", at = @At("TAIL"))
    private void silky$modifyAspect(DeltaTracker tickCounter, float tickDelta, float fov, CallbackInfo ci) {

        AspectRatio ar = Modules.get(AspectRatio.class);
        if (ar == null || !ar.isEnabled()) {
            return;
        }

        float mul = ar.getMultiplier();
        if (mul == 1.0f) {
            return;
        }

        Matrix4f m = gameRenderState.levelRenderState.cameraRenderState.projectionMatrix;
        if (m == null) return;

        // horizontal aspect stretch
        m.m00(m.m00() * mul);
    }

    @Inject(method = "extractCamera", at = @At("TAIL"))
    private void silky$captureProjectionFor2DOverlays(DeltaTracker tickCounter, float tickDelta, float fov, CallbackInfo ci) {
        if (gameRenderState == null || gameRenderState.levelRenderState == null) return;
        CameraRenderState cameraRenderState = gameRenderState.levelRenderState.cameraRenderState;
        if (cameraRenderState == null || cameraRenderState.viewRotationMatrix == null || cameraRenderState.projectionMatrix == null) return;

        PoseStack effectMatrices = new PoseStack();
        Matrix4f renderProjection = silky$buildWorldProjection(fov, effectMatrices, tickDelta);
        AspectRatio.applyToProjection(renderProjection);
        Matrix4f screenProjection = new Matrix4f(cameraRenderState.projectionMatrix);
        SilkyWorldMatrices.capture(
                cameraRenderState.viewRotationMatrix,
                renderProjection,
                screenProjection,
                cameraRenderState.pos != null ? cameraRenderState.pos : mainCamera.position()
        );
    }

    @Inject(method = "mainRenderTarget", at = @At("HEAD"), cancellable = true)
    private void silky$overrideWorldFramebuffer(CallbackInfoReturnable<com.mojang.blaze3d.pipeline.RenderTarget> cir) {
        com.mojang.blaze3d.pipeline.RenderTarget msaa = MsaaWorldTarget.getFramebufferOverride();
        if (msaa != null) {
            cir.setReturnValue(msaa);
        }
    }

    @Unique
    private void silky$renderWorldEngine(DeltaTracker tickCounter) {
        try (ProfilerPhase.Scope phaseScope = ProfilerPhase.scope("3d:world");
             TracyGpuProfiler.Scope gpuScope = TracyGpuProfiler.beginZone("3d:world")) {
            float tickDelta = tickCounter.getGameTimeDeltaPartialTick(true);
            float fov = mainCamera.getFov();
            Matrix4f capturedPosition = SilkyWorldMatrices.positionMatrix();
            Matrix4f capturedProjection = SilkyWorldMatrices.renderProjectionMatrix();
            boolean useCapturedMatrices = capturedPosition != null && capturedProjection != null;
            Matrix4f position = useCapturedMatrices
                    ? capturedPosition
                    : new Matrix4f().rotation(mainCamera.rotation().conjugate(new org.joml.Quaternionf()));
            RenderState.rendering3D = true;
            RenderState.tickDelta = tickDelta;
            Vec3 capturedCameraPos = SilkyWorldMatrices.cameraPosition();
            RenderState.cameraPos = capturedCameraPos != null ? capturedCameraPos : mainCamera.position();
            RenderState.cameraRotation.set(mainCamera.rotation());
            RenderState.cameraYaw = mainCamera.yRot();
            RenderState.cameraPitch = mainCamera.xRot();
            RenderState.cameraLook = Vec3.directionFromRotation(RenderState.cameraPitch, RenderState.cameraYaw).normalize();
            RenderState.cameraSubmersion = mainCamera.getFluidInCamera();
            RenderState.worldTranslucent = isWorldTranslucent(RenderState.cameraSubmersion);
            RenderState.frustum = mainCamera != null
                    ? mainCamera.getCapturedFrustum()
                    : null;

            if (SILKY_DEBUG_CLIP_SPACE) {
                var mv = com.mojang.blaze3d.systems.RenderSystem.getModelViewStack();
                mv.pushMatrix();
                mv.identity();

                MeshRenderer.setProjection(new Matrix4f().identity());
                RenderState.rendering3D = false;

                if (silky$renderer == null) {
                    silky$renderer = new Renderer3D(
                            SilkyRenderPipelines.WORLD_COLORED_LINES,
                            SilkyRenderPipelines.WORLD_COLORED
                    );
                }

                silky$renderer.begin();

                // Clip-space square + diagonals (NDC)
                silky$renderer.line(-0.8, -0.8, 0.0, 0.8, -0.8, 0.0, 255, 0, 0, 255);
                silky$renderer.line(0.8, -0.8, 0.0, 0.8, 0.8, 0.0, 0, 255, 0, 255);
                silky$renderer.line(0.8, 0.8, 0.0, -0.8, 0.8, 0.0, 0, 0, 255, 255);
                silky$renderer.line(-0.8, 0.8, 0.0, -0.8, -0.8, 0.0, 255, 255, 0, 255);
                silky$renderer.line(-0.8, -0.8, 0.0, 0.8, 0.8, 0.0, 255, 0, 255, 255);
                silky$renderer.line(0.8, -0.8, 0.0, -0.8, 0.8, 0.0, 0, 255, 255, 255);

                silky$renderer.render(new PoseStack());

                mv.popMatrix();
                RenderState.rendering3D = false;
                return;
            }

            if (silky$renderer == null) {
                silky$renderer = new Renderer3D(
                        SilkyRenderPipelines.WORLD_COLORED_LINES,
                        SilkyRenderPipelines.WORLD_COLORED
                );
            }

            if (silky$depthRenderer == null) {
                silky$depthRenderer = new Renderer3D(
                        SilkyRenderPipelines.WORLD_COLORED_LINES_DEPTH,
                        SilkyRenderPipelines.WORLD_COLORED_DEPTH
                );
            }

            var mv = com.mojang.blaze3d.systems.RenderSystem.getModelViewStack();
            mv.pushMatrix();
            if (IrisSilkyFrameHooks.isRenderingAfterIrisFinalization()) {
                mv.identity();
            }
            mv.mul(position);
            silky$matrices.pushPose();
            Matrix4f projection = useCapturedMatrices
                    ? capturedProjection
                    : silky$buildWorldProjection(fov, silky$matrices, tickDelta);
            MeshRenderer.setProjection(projection);
            SilkyRenderSystem.beginFrame(
                    tickDelta,
                    tickCounter.getGameTimeDeltaTicks(),
                    tickCounter.getRealtimeDeltaTicks(),
                    projection,
                    new Matrix4f(mv)
            );
            try (RenderPhaseScope silky$beforeTranslucentPhase = SilkyRenderSystem.phase(RenderPhase.WORLD_BEFORE_TRANSLUCENT)) {
                RenderState.worldProjection.set(projection);
                RenderState.frustum = new Frustum(position, projection);
                RenderState.frustum.prepare(RenderState.cameraPos.x, RenderState.cameraPos.y, RenderState.cameraPos.z);

                if (!useCapturedMatrices) {
                    Matrix4f inverseBob = new Matrix4f(silky$matrices.last().pose()).invert();
                    mv.mul(inverseBob);
                }

                silky$renderer.begin();
                silky$depthRenderer.begin();

                if (SILKY_DEBUG_PRIMITIVE) {
                    Vec3 camPos = mainCamera.position();
                    float yaw = mainCamera.yRot() * ((float) Math.PI / 180.0f);
                    float pitch = mainCamera.xRot() * ((float) Math.PI / 180.0f);
                    Vec3 forward = new Vec3(
                            -Mth.sin(yaw) * Mth.cos(pitch),
                            -Mth.sin(pitch),
                            Mth.cos(yaw) * Mth.cos(pitch)
                    );

                    Vec3 center = camPos.add(forward.scale(3.0));
                    double r = 0.5;
                    double x1 = center.x - r, x2 = center.x + r;
                    double y1 = center.y - r, y2 = center.y + r;
                    double z1 = center.z - r, z2 = center.z + r;
                    double ox1 = -1.0, oy1 = -1.0, oz1 = -1.0;
                    double ox2 = 1.0, oy2 = 1.0, oz2 = 1.0;

                    // Wire cube (no depth) and depth variant
                    Renderer3D[] targets = new Renderer3D[]{silky$renderer, silky$depthRenderer};
                    for (Renderer3D rdr : targets) {
                        silky$drawDebugCube(rdr, x1, y1, z1, x2, y2, z2);
                        silky$drawDebugCube(rdr, ox1, oy1, oz1, ox2, oy2, oz2);
                    }

                    long now = System.currentTimeMillis();
                    if (now - SILKY_DEBUG_LAST_MS >= SILKY_DEBUG_INTERVAL_MS) {
                        SILKY_DEBUG_LAST_MS = now;
                        Matrix4f mvMat = com.mojang.blaze3d.systems.RenderSystem.getModelViewMatrixCopy();
                        DebugLog.renderThread(
                                "[Silky] Debug diag: cam=" + camPos
                                        + " proj=[" + projection.m00() + "," + projection.m11() + "," + projection.m22() + "," + projection.m33() + "]"
                                        + " mv=[" + mvMat.m30() + "," + mvMat.m31() + "," + mvMat.m32() + "]"
                        );
                    }
                }

                try (ProfilerPhase.Scope phase = ProfilerPhase.scope("3d:modules_before_translucent");
                     RenderProfiler3D.Section ignored = RenderProfiler3D.section("modules_before_translucent")) {
                    ModuleManager.renderWorldEngine(WorldPhase.BEFORE_TRANSLUCENT, silky$renderer, silky$depthRenderer, tickDelta);
                    AddonRenderPipelineManager.render3D(SilkyRenderStage.WORLD_BEFORE_TRANSLUCENT,
                            WorldPhase.BEFORE_TRANSLUCENT, silky$renderer, silky$depthRenderer, silky$matrices, tickDelta);
                }

                try (ProfilerPhase.Scope phase = ProfilerPhase.scope("3d:flush_before_translucent");
                     TracyGpuProfiler.Scope gpuFlush = TracyGpuProfiler.beginZone("3d:flush_before_translucent");
                     RenderProfiler3D.Section ignored = RenderProfiler3D.section("flush_before_translucent")) {
                    silky$renderer.render(silky$matrices);
                    silky$depthRenderer.render(silky$matrices);
                }
            }

            // Start a new batch for END_MAIN so overlays render above grass.
            silky$renderer.begin();
            silky$depthRenderer.begin();

            try (RenderPhaseScope silky$afterTranslucentPhase = SilkyRenderSystem.phase(RenderPhase.WORLD_AFTER_TRANSLUCENT)) {
                try (ProfilerPhase.Scope phase = ProfilerPhase.scope("3d:modules_end_main");
                     RenderProfiler3D.Section ignored = RenderProfiler3D.section("modules_end_main")) {
                    ModuleManager.renderWorldEngine(WorldPhase.END_MAIN, silky$renderer, silky$depthRenderer, tickDelta);
                    AddonRenderPipelineManager.render3D(SilkyRenderStage.WORLD_END_MAIN,
                            WorldPhase.END_MAIN, silky$renderer, silky$depthRenderer, silky$matrices, tickDelta);

                    // Keep world billboards in the same END_MAIN world stage, but record them
                    // after ordinary translucent effects/particles. Billboard primitives use
                    // DepthMode.NONE, so the final ordering is deterministic without moving
                    // them into a HUD/post-process layer.
                    ModuleManager.renderWorldEngine(WorldPhase.END_MAIN_BILLBOARD,
                            silky$renderer, silky$depthRenderer, tickDelta);
                }

                try (ProfilerPhase.Scope phase = ProfilerPhase.scope("3d:flush_end_main");
                     TracyGpuProfiler.Scope gpuFlush = TracyGpuProfiler.beginZone("3d:flush_end_main");
                     RenderProfiler3D.Section ignored = RenderProfiler3D.section("flush_end_main")) {
                    silky$renderer.render(silky$matrices);
                    silky$depthRenderer.render(silky$matrices);
                }
            }
            silky$matrices.popPose();
            mv.popMatrix();
            RenderState.rendering3D = false;
        }
    }


    @Inject(method = "renderLevel", at = @At("HEAD"))
    private void silky$beginWorldMsaa(DeltaTracker tickCounter, CallbackInfo ci) {
        SilkyWorldMatrices.reset();
        if (VisualPreviewRuntime.isActive()) return;
        IrisSilkyFrameHooks.beginRenderLevel(tickCounter);
        int samples = MainConfig.get().getMsaa3dSamples();
        MsaaWorldTarget.begin(minecraft, samples, silky$needsResolvedMainDepth());
    }

    @Inject(method = "renderLevel", at = @At("RETURN"))
    private void silky$endIrisFrameHooks(DeltaTracker tickCounter, CallbackInfo ci) {
        if (VisualPreviewRuntime.isActive()) return;
        IrisSilkyFrameHooks.endRenderLevel(tickCounter);
    }

    @Redirect(
            method = "renderLevel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/LevelRenderer;render(Lcom/mojang/blaze3d/resource/GraphicsResourceAllocator;Lnet/minecraft/client/DeltaTracker;ZLnet/minecraft/client/renderer/state/level/CameraRenderState;Lorg/joml/Matrix4fc;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lorg/joml/Vector4f;Z)V"
            )
    )
    private void silky$skipWorldForVisualPreview(LevelRenderer renderer,
                                                      GraphicsResourceAllocator resources,
                                                      DeltaTracker tickCounter,
                                                      boolean renderBlockOutline,
                                                      CameraRenderState camera,
                                                      Matrix4fc positionMatrix,
                                                      GpuBufferSlice fog,
                                                      Vector4f clearColor,
                                                      boolean renderSky) {
        if (VisualPreviewRuntime.isActive()) return;
        renderer.render(resources, tickCounter, renderBlockOutline, camera, positionMatrix, fog, clearColor, renderSky);
    }

    @WrapOperation(
            method = "renderLevel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/ProjectionMatrixBuffer;getBuffer(Lorg/joml/Matrix4f;)Lcom/mojang/blaze3d/buffers/GpuBufferSlice;"
            )
    )
    private GpuBufferSlice silky$captureFullWorldMatrices(ProjectionMatrixBuffer instance,
                                                               Matrix4f renderProjectionMatrix,
                                                               Operation<GpuBufferSlice> original) {
        if (renderProjectionMatrix != null
                && gameRenderState != null
                && gameRenderState.levelRenderState != null
                && gameRenderState.levelRenderState.cameraRenderState != null
                && gameRenderState.levelRenderState.cameraRenderState.viewRotationMatrix != null
                && gameRenderState.levelRenderState.cameraRenderState.projectionMatrix != null) {
            CameraRenderState cameraRenderState = gameRenderState.levelRenderState.cameraRenderState;
            SilkyWorldMatrices.capture(
                    cameraRenderState.viewRotationMatrix,
                    renderProjectionMatrix,
                    cameraRenderState.projectionMatrix,
                    cameraRenderState.pos != null ? cameraRenderState.pos : mainCamera.position()
            );
        }
        return original.call(instance, renderProjectionMatrix);
    }

    @Inject(
            method = "renderLevel",
            at = @At(
                    value = "INVOKE_STRING",
                    target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V",
                    args = "ldc=hand",
                    shift = At.Shift.AFTER
            )
    )
    private void silky$postProcess(DeltaTracker tickCounter, CallbackInfo ci) {
        if (VisualPreviewRuntime.isActive()) return;
        if (IrisRuntime.isShaderpackRendererActive()
                && IrisCompatibilityGuards.deferIrisFinalizationForSecondHandScene()) {
            return;
        }
        silky$renderPreHandPostProcess(tickCounter);
    }

    @Unique
    private void silky$renderPreHandPostProcess(DeltaTracker tickCounter) {
        try (ProfilerPhase.Scope profilerScope = ProfilerPhase.scope("3d:post_pre_hand");
            TracyGpuProfiler.Scope gpuScope = TracyGpuProfiler.beginZone("3d:post_pre_hand")) {
            if (minecraft != null) {
                try (ProfilerPhase.Scope ignored = ProfilerPhase.scope("3d:pre_hand_resolve");
                     TracyGpuProfiler.Scope ignoredGpu = TracyGpuProfiler.beginZone("3d:pre_hand:resolve")) {
                    boolean needsResolvedDepth = !IrisRuntime.isShaderpackRendererActive()
                            && silky$needsResolvedMainDepth();
                    boolean capturedMsaaDepth = needsResolvedDepth
                            && MsaaWorldTarget.isActive()
                            && WorldSceneDepth.captureResolvedMain(MsaaWorldTarget.getMsaaFramebuffer());
                    MsaaWorldTarget.resolveToMain(minecraft);
                    if (needsResolvedDepth && !capturedMsaaDepth) {
                        WorldSceneDepth.captureResolvedMain(minecraft.gameRenderer.mainRenderTarget());
                    }
                }
                try (ProfilerPhase.Scope ignored = ProfilerPhase.scope("3d:visual_stack");
                     TracyGpuProfiler.Scope ignoredGpu = TracyGpuProfiler.beginZone("3d:visual_stack")) {
                    SilkyVisuals.renderWorldBase(minecraft, tickCounter.getGameTimeDeltaPartialTick(true));
                }
            }
            try (RenderPhaseScope silky$postPreHandPhase = SilkyRenderSystem.phase(RenderPhase.WORLD_POST_PRE_HAND)) {
                PostProcessManager.renderAll(PostProcessPass.Phase.PRE_HAND,
                        tickCounter.getGameTimeDeltaPartialTick(true));
                silky$renderSilkyWorldAfterPreHandPostProcess(tickCounter);
            }
        }
    }

    @Unique
    private void silky$renderSilkyWorldAfterPreHandPostProcess(DeltaTracker tickCounter) {
        if (minecraft == null) {
            silky$renderWorldEngine(tickCounter);
            silky$renderPostProcessWorld(tickCounter);
            return;
        }

        try (ProfilerPhase.Scope ignored = ProfilerPhase.scope("3d:world_engine_target");
             TracyGpuProfiler.Scope ignoredGpu = TracyGpuProfiler.beginZone("3d:world_engine_target")) {
            MsaaWorldTarget.begin(minecraft, MainConfig.get().getMsaa3dSamples(), false, true);
            try {
                silky$renderWorldEngine(tickCounter);
                silky$renderPostProcessWorld(tickCounter);
            } finally {
                MsaaWorldTarget.resolveToMain(minecraft);
            }
        }
    }

    @Override
    public void silky$renderAfterIrisFinalization(DeltaTracker tickCounter) {
        if (VisualPreviewRuntime.isActive()) return;
        silky$renderPreHandPostProcess(tickCounter);
    }

    @Inject(
            method = "renderLevel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/feature/FeatureRenderDispatcher;renderAllFeatures(Lnet/minecraft/client/renderer/SubmitNodeStorage;)V",
                    shift = At.Shift.AFTER
            )
    )
    private void silky$postProcessAfterHand(DeltaTracker tickCounter, CallbackInfo ci) {
        if (VisualPreviewRuntime.isActive()) return;
        try (ProfilerPhase.Scope profilerScope = ProfilerPhase.scope("3d:post_after_hand");
             TracyGpuProfiler.Scope gpuScope = TracyGpuProfiler.beginZone("3d:post_after_hand")) {
            if (minecraft != null) {
                MsaaWorldTarget.resolveToMain(minecraft);
            }
            try (RenderPhaseScope silky$postHandPhase = SilkyRenderSystem.phase(RenderPhase.WORLD_POST_HAND)) {
                PostProcessManager.renderAll(PostProcessPass.Phase.POST_HAND,
                        tickCounter.getGameTimeDeltaPartialTick(true));
            }
        }
    }

    @Unique
    private void silky$renderPostProcessWorld(DeltaTracker tickCounter) {
        try (ProfilerPhase.Scope phaseScope = ProfilerPhase.scope("3d:world_post");
             TracyGpuProfiler.Scope gpuScope = TracyGpuProfiler.beginZone("3d:world_post")) {
            float tickDelta = tickCounter.getGameTimeDeltaPartialTick(true);
            float fov = mainCamera.getFov();
            Matrix4f capturedPosition = SilkyWorldMatrices.positionMatrix();
            Matrix4f capturedProjection = SilkyWorldMatrices.renderProjectionMatrix();
            boolean useCapturedMatrices = capturedPosition != null && capturedProjection != null;
            Matrix4f position = useCapturedMatrices
                    ? capturedPosition
                    : new Matrix4f().rotation(mainCamera.rotation().conjugate(new org.joml.Quaternionf()));

            RenderState.rendering3D = true;
            RenderState.tickDelta = tickDelta;
            Vec3 capturedCameraPos = SilkyWorldMatrices.cameraPosition();
            RenderState.cameraPos = capturedCameraPos != null ? capturedCameraPos : mainCamera.position();
            RenderState.cameraRotation.set(mainCamera.rotation());
            RenderState.cameraYaw = mainCamera.yRot();
            RenderState.cameraPitch = mainCamera.xRot();

            if (silky$postRenderer == null) {
                silky$postRenderer = new Renderer3D(
                        SilkyRenderPipelines.WORLD_COLORED_LINES,
                        SilkyRenderPipelines.WORLD_COLORED
                );
            }

            if (silky$postDepthRenderer == null) {
                silky$postDepthRenderer = new Renderer3D(
                        SilkyRenderPipelines.WORLD_COLORED_LINES_DEPTH,
                        SilkyRenderPipelines.WORLD_COLORED_DEPTH
                );
            }

            var mv = com.mojang.blaze3d.systems.RenderSystem.getModelViewStack();
            mv.pushMatrix();
            if (IrisSilkyFrameHooks.isRenderingAfterIrisFinalization()) {
                mv.identity();
            }
            mv.mul(position);
            silky$postMatrices.pushPose();
            Matrix4f projection = useCapturedMatrices
                    ? capturedProjection
                    : silky$buildWorldProjection(fov, silky$postMatrices, tickDelta);
            MeshRenderer.setProjection(projection);
            SilkyRenderSystem.beginFrame(
                    tickDelta,
                    tickCounter.getGameTimeDeltaTicks(),
                    tickCounter.getRealtimeDeltaTicks(),
                    projection,
                    new Matrix4f(mv)
            );
            try (RenderPhaseScope silky$postWorldPhase = SilkyRenderSystem.phase(RenderPhase.WORLD_POST_PRE_HAND)) {
                RenderState.worldProjection.set(projection);
                RenderState.frustum = new Frustum(position, projection);
                RenderState.frustum.prepare(RenderState.cameraPos.x, RenderState.cameraPos.y, RenderState.cameraPos.z);

                if (!useCapturedMatrices) {
                    Matrix4f inverseBob = new Matrix4f(silky$postMatrices.last().pose()).invert();
                    mv.mul(inverseBob);
                }

                silky$postRenderer.begin();
                silky$postDepthRenderer.begin();

                try (ProfilerPhase.Scope phase = ProfilerPhase.scope("3d:post_modules");
                     RenderProfiler3D.Section ignored = RenderProfiler3D.section("modules")) {
                    ModuleManager.renderWorldEngine(WorldPhase.AFTER_POST_PROCESS, silky$postRenderer, silky$postDepthRenderer, tickDelta);
                    AddonRenderPipelineManager.render3D(SilkyRenderStage.WORLD_POST_PROCESS,
                            WorldPhase.AFTER_POST_PROCESS, silky$postRenderer, silky$postDepthRenderer, silky$postMatrices, tickDelta);
                }

                try (ProfilerPhase.Scope phase = ProfilerPhase.scope("3d:post_flush");
                     TracyGpuProfiler.Scope gpuFlush = TracyGpuProfiler.beginZone("3d:post_flush");
                     RenderProfiler3D.Section ignored = RenderProfiler3D.section("flush")) {
                    silky$postRenderer.render(silky$postMatrices);
                    silky$postDepthRenderer.render(silky$postMatrices);
                }
            }
            silky$postMatrices.popPose();
            mv.popMatrix();
            RenderState.rendering3D = false;
        }
    }

    @Unique
    private Matrix4f silky$buildWorldProjection(float fov, PoseStack viewEffectMatrices, float tickDelta) {
        CameraRenderState cameraRenderState = new CameraRenderState();
        mainCamera.extractRenderState(cameraRenderState, fov);
        Matrix4f projection = new Matrix4f(cameraRenderState.projectionMatrix);
        silky$applyViewEffectMatrices(viewEffectMatrices, tickDelta);
        projection.mul(viewEffectMatrices.last().pose());
        silky$applyNauseaProjection(projection, tickDelta);
        return projection;
    }

    @Unique
    private void silky$applyViewEffectMatrices(PoseStack matrices, float cameraTickProgress) {
        NoRender noRender = Modules.get(NoRender.class);
        CameraRenderState cameraRenderState = new CameraRenderState();
        mainCamera.extractRenderState(cameraRenderState, mainCamera.getFov());
        if (noRender == null || !noRender.off("camera_shake")) {
            bobHurt(cameraRenderState, matrices);
        }

        boolean freecamEnabled = Modules.get(Freecam.class) != null && Modules.get(Freecam.class).isEnabled();
        if (minecraft.options.bobView().get()
                && !freecamEnabled
                && (noRender == null || !noRender.off("view_bob"))) {
            bobView(cameraRenderState, matrices);
        }
    }

    @Unique
    private void silky$applyNauseaProjection(Matrix4f projection, float tickDelta) {
        LocalPlayer player = minecraft.player;
        if (player == null) return;

        float distortionScale = minecraft.options.screenEffectScale().get().floatValue();
        if (distortionScale <= 0.0f) return;

        float nauseaIntensity = 0.0f;
        if (player instanceof LocalPlayerAccessor accessor) {
            nauseaIntensity = Mth.lerp(
                    tickDelta,
                    accessor.silky$getLastNauseaIntensity(),
                    accessor.silky$getNauseaIntensity()
            );
        }

        float nauseaEffect = player.getEffectBlendFactor(MobEffects.NAUSEA, tickDelta);
        float strength = Math.max(nauseaIntensity, nauseaEffect) * (distortionScale * distortionScale);
        if (strength <= 0.0f) return;

        float scale = 5.0f / (strength * strength + 5.0f) - strength * 0.04f;
        scale *= scale;
        Vector3f axis = new Vector3f(0.0f, Mth.SQRT_OF_TWO / 2.0f, Mth.SQRT_OF_TWO / 2.0f);
        float angle = (this.spinningEffectTime + tickDelta * this.spinningEffectSpeed) * Mth.DEG_TO_RAD;
        projection.rotate(angle, axis);
        projection.scale(1.0f / scale, 1.0f, 1.0f);
        projection.rotate(-angle, axis);
    }

    @Inject(method = "renderItemInHand(Lnet/minecraft/client/renderer/state/level/CameraRenderState;FLorg/joml/Matrix4fc;)V", at = @At("HEAD"), cancellable = true)
    private void freecam$hand(CameraRenderState cameraRenderState, float tickDelta, Matrix4fc positionMatrix, CallbackInfo ci) {
        if (VisualPreviewRuntime.isActive()) {
            ci.cancel();
            return;
        }
        Freecam fc = Modules.get(Freecam.class);
        if (fc != null && fc.isEnabled() && !fc.renderHand()) {
            ci.cancel();
        }
    }

    @WrapOperation(
            method = "renderItemInHand(Lnet/minecraft/client/renderer/state/level/CameraRenderState;FLorg/joml/Matrix4fc;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/feature/FeatureRenderDispatcher;renderAllFeatures(Lnet/minecraft/client/renderer/SubmitNodeStorage;)V"
            )
    )
    private void silky$renderChamsHandPreparedFrame(FeatureRenderDispatcher dispatcher,
                                                        SubmitNodeStorage storage,
                                                        Operation<Void> original,
                                                        CameraRenderState cameraRenderState,
                                                        float tickDelta,
                                                        Matrix4fc positionMatrix) {
        Chams module = Modules.get(Chams.class);
        SubmitNodeStorage snapshot = module != null ? module.snapshotPreparedHandScene(storage) : null;

        // Render Minecraft's live hand storage first. renderAllFeatures() consumes it in 26.2.
        original.call(dispatcher, storage);

        // Chams renders only the isolated snapshot afterwards; it must never consume the live storage.
        if (module != null && snapshot != null) {
            module.renderPreparedHandScene(snapshot);
        }
    }

    @Inject(method = "render(Lnet/minecraft/client/DeltaTracker;Z)V", at = @At("TAIL"))
    private void silky$flushQueuedButtons(DeltaTracker tickCounter, boolean tick, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || ClientScreen.current() == null) return;
        if (!BetterButtons.hasPending()) {
            BetterButtons.renderTooltip();
            return;
        }
        BetterButtons.flush();
        BetterButtons.renderTooltip();
    }

    @Inject(
            method = "render(Lnet/minecraft/client/DeltaTracker;Z)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/render/GuiRenderer;render()V",
                    shift = At.Shift.BEFORE
            )
    )
    private void silky$captureWorldForHudGlass(DeltaTracker tickCounter, boolean tick, CallbackInfo ci) {
        MenuBackgroundRenderer.drainDeferred(minecraft);
        Renderer2D.prepareDeferredUiItems();
        Renderer2D.captureWorldGlassSource();
        if (AddonRenderPipelineManager.hasActiveCallbacks(SilkyRenderStage.SCREEN_BEFORE_VANILLA_GUI)) {
            float tickDelta = tickCounter.getGameTimeDeltaPartialTick(true);
            Renderer2D.COLOR.begin();
            AddonRenderPipelineManager.render2D(SilkyRenderStage.SCREEN_BEFORE_VANILLA_GUI,
                    null, Renderer2D.COLOR, TextRenderer.get(), null, tickDelta);
            Renderer2D.COLOR.render();
        }
        Renderer2D.drainDeferred2D(Renderer2D.Deferred2DLayer.BEFORE_VANILLA_GUI);
        silky$drainHudLayersBelowScreenGui();
    }

    /**
     * Gui.extractRenderState() extracts vanilla HUD first and the current Screen after it.
     * Normal gameplay keeps Silky HUD spliced into that HUD order through HudDeferredGuiElement
     * markers. When a real Screen is open, however, the screen must remain the top GUI owner:
     * HUD/world overlaysare drained before GuiRenderer.render(),
     * preserving their internal order while keeping container/inventory GUI above them.
     * Explicit SCREEN_TOP/AFTER_VANILLA_GUI overlays (Dynamic Island, ClickGui top layer, etc.) are
     * intentionally not drained here.
     */
    @Unique
    private static void silky$drainHudLayersBelowScreenGui() {
        if (ClientScreen.current() == null) {
            return;
        }

        Renderer2D.drainDeferred2D(Renderer2D.Deferred2DLayer.HUD_FIRST);
        Renderer2D.drainDeferred2D(Renderer2D.Deferred2DLayer.HUD_BEFORE_MISC_OVERLAYS);
        Renderer2D.drainDeferred2D(Renderer2D.Deferred2DLayer.HUD_AFTER_MISC_OVERLAYS);
        Renderer2D.drainDeferred2D(Renderer2D.Deferred2DLayer.HUD_AFTER_BOSS_BAR);
        Renderer2D.drainDeferred2D(Renderer2D.Deferred2DLayer.HUD_BEFORE_DEMO_TIMER);
        Renderer2D.drainDeferred2D(Renderer2D.Deferred2DLayer.HUD_BEFORE_CHAT);
        Renderer2D.drainDeferred2D(Renderer2D.Deferred2DLayer.HUD_AFTER_SUBTITLES);
        Renderer2D.drainDeferred2D(Renderer2D.Deferred2DLayer.HUD_LAST);
    }

    @Inject(
            method = "render(Lnet/minecraft/client/DeltaTracker;Z)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/render/GuiRenderer;render()V",
                    shift = At.Shift.AFTER
            )
    )
    private void silky$debug2dImmediateAfterGui(DeltaTracker tickCounter, boolean tick, CallbackInfo ci) {
        Renderer2D.drainDeferred2D(Renderer2D.Deferred2DLayer.AFTER_VANILLA_GUI);
        if (AddonRenderPipelineManager.hasActiveCallbacks(SilkyRenderStage.SCREEN_AFTER_VANILLA_GUI)) {
            float tickDelta = tickCounter.getGameTimeDeltaPartialTick(true);
            Renderer2D.COLOR.begin();
            AddonRenderPipelineManager.render2D(SilkyRenderStage.SCREEN_AFTER_VANILLA_GUI,
                    null, Renderer2D.COLOR, TextRenderer.get(), null, tickDelta);
            Renderer2D.COLOR.render();
        }
        // Debug 2D probe intentionally disabled after projection validation.
        // RenderThread2DDebugRenderer.renderImmediateAfterGui(tickCounter);
    }

}
