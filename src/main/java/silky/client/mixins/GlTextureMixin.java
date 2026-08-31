/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.mixins;

import com.mojang.blaze3d.opengl.GlTexture;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import silky.client.mixininterface.IMsaaTexture;
import silky.client.render.engine.core.SilkyRenderSystem;

@Mixin(GlTexture.class)
public abstract class GlTextureMixin implements IMsaaTexture {
    @Final
    @Shadow
    protected int id;

    @Unique
    private int silky$samples = 1;

    @Override
    public void silky$setSamples(int samples) {
        silky$samples = samples;
    }

    @Override
    public int silky$getSamples() {
        return silky$samples;
    }

    @Override
    public boolean silky$isMsaa() {
        return silky$samples > 1;
    }

    @Override
    public int silky$getGlId() {
        return id;
    }

    @Inject(method = "destroyImmediately", at = @At("HEAD"))
    private void silky$onFree(CallbackInfo ci) {
        SilkyRenderSystem.rhi().msaa().unregisterTexture(id);
    }
}




