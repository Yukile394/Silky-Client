/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.mixins;

import net.minecraft.client.renderer.DynamicUniformStorage;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import silky.client.util.logging.DebugLog;

@Mixin(DynamicUniformStorage.class)
public abstract class DynamicUniformStorageMixin {
    @Final
    @Shadow
    private String label;

    @Redirect(
            method = {"writeUniform", "writeUniforms"},
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/slf4j/Logger;info(Ljava/lang/String;[Ljava/lang/Object;)V"
            )
    )
    private void silky$redirectResizeLog(Logger logger, String format, Object[] args) {
        if (label != null && label.startsWith("Silky")) {
            String message = "Resizing " + args[0]
                    + ", capacity limit of " + args[1]
                    + " reached during a single frame. New capacity will be " + args[2] + ".";
            DebugLog.renderThread("%s", message);
            return;
        }
        logger.info(format, args);
    }
}
