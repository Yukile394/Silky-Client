/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.mixins.accessors;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntityRenderState.class)
public interface LivingEntityRenderStateAccessor {

    @Accessor("bodyRot")
    void silky$setBodyYaw(float bodyYaw);

    @Accessor("yRot")
    void silky$setRelativeHeadYaw(float relativeHeadYaw);

    @Accessor("xRot")
    void silky$setPitch(float pitch);
}
