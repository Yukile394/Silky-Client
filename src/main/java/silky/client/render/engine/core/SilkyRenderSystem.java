/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.render.engine.core;

import com.mojang.blaze3d.systems.RenderSystem;
import silky.client.render.engine.compat.immediatelyfast.ImmediatelyFastRuntime;
import silky.client.render.engine.compat.immediatelyfast.ImmediatelyFastRuntimeSnapshot;
import silky.client.render.engine.renderer.Renderer3D;
import silky.client.render.engine.renderer.ui.ItemBatchRenderer;
import silky.client.render.iris.IrisRuntime;
import silky.client.render.iris.IrisRuntimeSnapshot;
import silky.client.render.sodium.SodiumTerrainInteropStatsSnapshot;
import silky.client.render.sodium.SodiumVisibilityStatsSnapshot;
import org.joml.Matrix4f;
import silky.client.render.engine.RenderState;
import silky.client.render.engine.core.policy.LightPolicy;
import silky.client.render.engine.core.policy.VanillaWorldFogProvider;
import silky.client.render.engine.depth.WorldSceneDepth;
import silky.client.render.engine.framegraph.SilkyFrameGraph;
import silky.client.render.engine.profiler.FrameStutterProfiler;
import silky.client.render.engine.profiler.RenderFrameProfiler;
import silky.client.render.engine.rhi.SilkyRhi;
import silky.client.render.engine.rhi.RhiStatsSnapshot;
import silky.client.render.engine.rhi.backend.SodiumGlBackend;
import silky.client.render.engine.rhi.backend.vulkan.SilkyVulkanBackend;
import silky.client.render.engine.rhi.resource.RenderResourceManager;
import silky.client.render.engine.rhi.resource.RenderResourceStatsSnapshot;
import silky.client.render.engine.rhi.uniform.SilkyUniformAllocator;
import silky.client.render.engine.rhi.uniform.UniformAllocatorStatsSnapshot;
import silky.client.render.engine.text.TextCommandStatsSnapshot;
import silky.client.render.engine.text.TextRenderSystem;
import silky.client.render.engine.world.WorldRenderStatsSnapshot;
import silky.client.render.sodium.SodiumFrameContext;
import silky.client.render.sodium.SodiumRenderBridge;
import silky.client.util.logging.DebugLog;

import java.util.Locale;

/**
 * Single owner of Silky render lifecycle.
 * - one Silky frame per Minecraft-presented frame;
 * - many scoped RenderPhase entries inside that frame;
 * - backend submission is flushed before framePresented(), not by random renderer classes;
 * - RenderState is a compatibility shim, not the lifecycle owner.
 */
public enum SilkyRenderSystem {
    ;
    private static final SodiumRenderBridge SODIUM = new SodiumRenderBridge();
    private static final SilkyFrameGraph FRAME_GRAPH = new SilkyFrameGraph();
    private static final SilkyUniformAllocator UNIFORMS = new SilkyUniformAllocator();

    private static SilkyRhi rhi;
    private static BackendKind backendKind = BackendKind.UNKNOWN;
    private static RenderFrameContext currentContext;
    private static long frameId;
    private static boolean initialized;
    private static boolean frameOpen;
    private static boolean submissionEnded;
    private static FrameLifecycle lifecycle = FrameLifecycle.IDLE;

    private enum BackendKind {
        UNKNOWN,
        GL,
        VULKAN
    }

    public static void init() {
        if (initialized) {
            ensureBackendMatchesDevice();
            return;
        }
        BackendKind desired = detectBackendKind();
        backendKind = desired == BackendKind.UNKNOWN ? BackendKind.GL : desired;
        rhi = createBackend(backendKind);
        initialized = true;
        DebugLog.renderThreadOnChange(
                "silky.rhi.backend",
                backendKind,
                "[SilkyRHI] backend initialized: %s",
                backendKind
        );
        ensureBackendMatchesDevice();
    }

    private static SilkyRhi createBackend(BackendKind kind) {
        if (kind == BackendKind.VULKAN) {
            return new SilkyVulkanBackend();
        }
        return new SodiumGlBackend();
    }

    private static void ensureBackendMatchesDevice() {
        if (!initialized || rhi == null) return;
        BackendKind desired = detectBackendKind();
        if (desired == BackendKind.UNKNOWN || desired == backendKind) return;

        if (frameOpen) {
            DebugLog.warnOnChange(
                    "silky.rhi.backend.switch.deferred",
                    backendKind + "->" + desired + "|frameOpen",
                    "[SilkyRHI] backend switch deferred until frame end: %s -> %s",
                    backendKind,
                    desired
            );
            return;
        }

        SilkyRhi previous = rhi;
        BackendKind previousKind = backendKind;
        SilkyRhi next;
        try {
            next = createBackend(desired);
        } catch (Throwable t) {
            DebugLog.error("[SilkyRHI] failed to create backend " + desired, t);
            return;
        }

        rhi = next;
        backendKind = desired;
        try {
            previous.close();
        } catch (Throwable t) {
            DebugLog.warnOnChange(
                    "silky.rhi.backend.old.close.failed",
                    previousKind + "|" + desired + "|" + t.getClass().getSimpleName(),
                    "[SilkyRHI] old backend close failed during switch %s -> %s: %s: %s",
                    previousKind,
                    desired,
                    t.getClass().getSimpleName(),
                    t.getMessage()
            );
        }
        DebugLog.renderThreadOnChange(
                "silky.rhi.backend",
                backendKind,
                "[SilkyRHI] backend switched: %s -> %s",
                previousKind,
                backendKind
        );
    }

    private static BackendKind detectBackendKind() {
        String backendName = null;
        try {
            var device = RenderSystem.tryGetDevice();
            if (device != null && device.getDeviceInfo() != null) {
                backendName = device.getDeviceInfo().backendName();
            }
        } catch (Throwable ignored) {
            // Fall through to RenderSystem backend description.
        }

        if (backendName == null || backendName.isBlank()) {
            try {
                backendName = RenderSystem.getBackendDescription();
            } catch (Throwable ignored) {
                backendName = null;
            }
        }

        if (backendName == null || backendName.isBlank()) return BackendKind.UNKNOWN;
        String normalized = backendName.toLowerCase(Locale.ROOT);
        if (normalized.contains("vulkan")) return BackendKind.VULKAN;
        if (normalized.contains("opengl") || normalized.contains("gl")) return BackendKind.GL;
        return BackendKind.UNKNOWN;
    }

    public static SilkyRhi rhi() {
        init();
        ensureBackendMatchesDevice();
        return rhi;
    }

    public static SodiumRenderBridge sodium() {
        return SODIUM;
    }

    public static RenderResourceManager resources() {
        return rhi().resources();
    }

    public static SilkyFrameGraph frameGraph() {
        return FRAME_GRAPH;
    }

    public static SilkyUniformAllocator uniforms() {
        return UNIFORMS;
    }

    public static RenderFrameContext currentContext() {
        return currentContext;
    }

    public static FrameLifecycle lifecycle() {
        return lifecycle;
    }

    /**
     * Opens the Silky frame if needed, otherwise refreshes camera/viewport data without advancing frameId.
     */
    public static RenderFrameContext beginFrame(float tickDelta, Matrix4f projection, Matrix4f modelView) {
        return beginFrame(tickDelta, tickDelta, tickDelta, projection, modelView);
    }

    public static RenderFrameContext beginFrame(float tickProgress,
                                                float frameDeltaTicks,
                                                float fixedDeltaTicks,
                                                Matrix4f projection,
                                                Matrix4f modelView) {
        SilkyRhi activeRhi = rhi();
        SodiumFrameContext sodiumFrame;
        if (!frameOpen) {
            frameId++;
            activeRhi.beginFrame(frameId);
            UNIFORMS.beginFrame(frameId);
            TextRenderSystem.beginFrame();
            sodiumFrame = SODIUM.beginFrame();
            RenderFrameProfiler.beginFrame(frameId);
            frameOpen = true;
            submissionEnded = false;
            lifecycle = FrameLifecycle.RECORDING;
        } else {
            sodiumFrame = SODIUM.currentFrameContext();
        }

        RenderPhase phase = currentContext != null ? currentContext.phase() : RenderPhase.NONE;
        RenderFrameContext ctx = new RenderFrameContext(
                frameId,
                tickProgress,
                frameDeltaTicks,
                fixedDeltaTicks,
                phase,
                CameraContext.capture(projection, modelView),
                ViewportContext.capture(),
                FramebufferContext.capture(),
                WorldSceneDepth::mainDepthView,
                VanillaWorldFogProvider.INSTANCE,
                LightPolicy.VANILLA,
                SODIUM.visibilityProvider(),
                sodiumFrame
        );
        currentContext = ctx;
        RenderState.applyContext(ctx);
        return ctx;
    }

    /**
     * Refreshes only timing data for phases that do not own the world camera/projection
     * themselves, for example Fabric HUD callbacks.
     */
    public static RenderFrameContext updateFrameTiming(float tickProgress,
                                                       float frameDeltaTicks,
                                                       float fixedDeltaTicks) {
        RenderFrameContext base = ensureFrameContext();
        currentContext = base.withTiming(tickProgress, frameDeltaTicks, fixedDeltaTicks);
        RenderState.applyContext(currentContext);
        return currentContext;
    }

    public static RenderFrameContext ensureFrameContext() {
        if (currentContext != null) return currentContext;
        return beginFrame(RenderState.tickProgress, RenderState.frameDeltaTicks, RenderState.fixedDeltaTicks, new Matrix4f(), new Matrix4f(RenderSystem.getModelViewStack()));
    }

    public static RenderPhaseScope phase(RenderPhase phase) {
        return phase(phase, null);
    }

    public static RenderPhaseScope phase(RenderPhase phase, String label) {
        RenderFrameContext before = currentContext;
        RenderFrameContext base = ensureFrameContext();
        RenderPhase next = phase == null ? RenderPhase.NONE : phase;
        currentContext = base.withPhase(next);
        RenderState.applyContext(currentContext);
        return new RenderPhaseScope(next, before, RenderFrameProfiler.phase(next, label));
    }

    static void restorePhase(RenderFrameContext previous) {
        currentContext = previous;
        if (currentContext != null) {
            RenderState.applyContext(currentContext);
        } else {
            RenderState.clearContextBackedState();
        }
    }

    public static void endRenderSubmission() {
        if (!initialized || !frameOpen || submissionEnded) return;
        try {
            ItemBatchRenderer.finishUiItemFrame();
            TextRenderSystem.flush();
            rhi.endRenderSubmission();
            lifecycle = FrameLifecycle.SUBMITTED;
        } finally {
            submissionEnded = true;
        }
    }

    /**
     * Called after the window surface has been presented. Rendering submission must already
     * be closed before GpuSurface.blitFromTexture(), otherwise late Silky draws miss the
     * presented frame and can leave stale main-target contents for the next frame.
     */
    public static void onFramePresented() {
        if (!initialized) return;
        try {
            if (!submissionEnded) {
                // Fallback for unusual renderFrame paths. Normal visible frames close in
                // GpuSurfaceMixin#silky$beforeSurfaceBlit, before the surface copy.
                endRenderSubmission();
            }
            rhi.framePresented();
            UNIFORMS.onFramePresented();
            if (FrameStutterProfiler.isEnabled()) {
                FrameStutterProfiler.onFramePresented(rhiStatsSnapshot(), uniformStatsSnapshot(), resourceStatsSnapshot());
            }
            RenderFrameProfiler.endFrame(null, null);
            lifecycle = FrameLifecycle.PRESENTED;
        } finally {
            currentContext = null;
            frameOpen = false;
            submissionEnded = false;
            lifecycle = FrameLifecycle.IDLE;
            RenderState.clearContextBackedState();
        }
    }

    public static RhiStatsSnapshot rhiStatsSnapshot() {
        return rhi().stats().snapshot();
    }

    public static SodiumVisibilityStatsSnapshot sodiumVisibilityStatsSnapshot() {
        return SODIUM.visibilityStatsSnapshot();
    }

    public static SodiumTerrainInteropStatsSnapshot sodiumTerrainStatsSnapshot() {
        return SODIUM.terrainInterop().statsSnapshot();
    }

    public static IrisRuntimeSnapshot irisSnapshot() {
        return IrisRuntime.snapshot();
    }

    public static ImmediatelyFastRuntimeSnapshot immediatelyFastSnapshot() {
        return ImmediatelyFastRuntime.snapshot();
    }

    public static UniformAllocatorStatsSnapshot uniformStatsSnapshot() {
        return UNIFORMS.statsSnapshot();
    }

    public static RenderResourceStatsSnapshot resourceStatsSnapshot() {
        return resources().statsSnapshot();
    }

    public static WorldRenderStatsSnapshot worldRenderStatsSnapshot() {
        return Renderer3D.worldStatsSnapshot();
    }

    public static TextCommandStatsSnapshot textStatsSnapshot() {
        return TextRenderSystem.statsSnapshot();
    }

    public static void shutdown() {
        if (!initialized) return;
        try {
            UNIFORMS.close();
            rhi.close();
        } finally {
            initialized = false;
            currentContext = null;
            frameOpen = false;
            submissionEnded = false;
            lifecycle = FrameLifecycle.IDLE;
            rhi = null;
            RenderState.clearContextBackedState();
        }
    }
}
