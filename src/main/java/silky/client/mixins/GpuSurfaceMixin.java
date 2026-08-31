/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.mixins;

import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.CommandEncoderBackend;
import com.mojang.blaze3d.systems.GpuSurface;
import com.mojang.blaze3d.systems.GpuSurfaceBackend;
import com.mojang.blaze3d.systems.SurfaceException;
import com.mojang.blaze3d.textures.GpuTextureView;
import silky.client.render.engine.animation.AnimationUtility;
import silky.client.render.engine.core.SilkyRenderSystem;
import silky.client.render.engine.profiler.ProfilerPhase;
import silky.client.render.engine.profiler.TracyGpuProfiler;
import silky.client.render.engine.renderer.Renderer2D;
import silky.client.util.FastFps;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GpuSurface.class)
public abstract class GpuSurfaceMixin {
    /**
     * Last safe point before Minecraft copies the main render target into the
     * window surface. Silky submission must be closed before the blit.
     */
    @Inject(method = "blitFromTexture", at = @At("HEAD"))
    private void silky$beforeSurfaceBlit(CommandEncoder encoder, GpuTextureView textureView, CallbackInfo info) {
        SilkyRenderSystem.endRenderSubmission();
    }

    @Inject(method = "present", at = @At("HEAD"))
    private void silky$onPresentHead(CallbackInfo info) {
        FastFps.onFrame();
        AnimationUtility.onFrame();
    }

    @Inject(method = "present", at = @At("TAIL"))
    private void silky$onPresentTail(CallbackInfo info) {
        if (TracyGpuProfiler.isEnabled()) {
            TracyGpuProfiler.onFrameEnd();
        }
        SilkyRenderSystem.onFramePresented();
        Renderer2D.getBatchStats().onFrameStart();
        Renderer2D.invalidateWorldGlassSource();
    }

    @Redirect(
            method = "configure",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/GpuSurfaceBackend;configure(Lcom/mojang/blaze3d/systems/GpuSurface$Configuration;)V")
    )
    private void silky$profileSurfaceConfigure(GpuSurfaceBackend backend, GpuSurface.Configuration configuration) throws SurfaceException {
        try (ProfilerPhase.Scope ignored = ProfilerPhase.scope("surface:configure")) {
            backend.configure(configuration);
        }
    }

    @Redirect(
            method = "acquireNextTexture",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/GpuSurfaceBackend;acquireNextTexture()V")
    )
    private void silky$profileSurfaceAcquire(GpuSurfaceBackend backend) throws SurfaceException {
        try (ProfilerPhase.Scope ignored = ProfilerPhase.scope("surface:acquire")) {
            backend.acquireNextTexture();
        }
    }

    @Redirect(
            method = "blitFromTexture",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/GpuSurfaceBackend;blitFromTexture(Lcom/mojang/blaze3d/systems/CommandEncoderBackend;Lcom/mojang/blaze3d/textures/GpuTextureView;)V")
    )
    private void silky$profileSurfaceBlit(GpuSurfaceBackend backend, CommandEncoderBackend encoder, GpuTextureView textureView) {
        try (ProfilerPhase.Scope ignored = ProfilerPhase.scope("surface:blit")) {
            backend.blitFromTexture(encoder, textureView);
        }
    }

    @Redirect(
            method = "present",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/GpuSurfaceBackend;present()V")
    )
    private void silky$profileSurfacePresent(GpuSurfaceBackend backend) {
        try (ProfilerPhase.Scope ignored = ProfilerPhase.scope("surface:present")) {
            backend.present();
        }
    }
}
