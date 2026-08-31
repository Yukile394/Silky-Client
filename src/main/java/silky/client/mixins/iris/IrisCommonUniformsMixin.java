/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.mixins.iris;

import net.irisshaders.iris.gl.uniform.UniformHolder;
import net.irisshaders.iris.shaderpack.IdMap;
import net.irisshaders.iris.shaderpack.properties.PackDirectives;
import net.irisshaders.iris.uniforms.FrameUpdateNotifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import silky.client.render.iris.SilkyIrisUniforms;

@Pseudo
@Mixin(targets = "net.irisshaders.iris.uniforms.CommonUniforms", remap = false)
public abstract class IrisCommonUniformsMixin {
    @Inject(method = "addNonDynamicUniforms", at = @At("TAIL"), remap = false)
    private static void silky$addSilkyUniforms(UniformHolder uniforms,
                                                       IdMap idMap,
                                                       PackDirectives packDirectives,
                                                       FrameUpdateNotifier updateNotifier,
                                                       CallbackInfo ci) {
        SilkyIrisUniforms.add(uniforms);
    }
}
