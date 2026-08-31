/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.mixins;

import net.minecraft.client.renderer.entity.state.ItemEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import silky.client.mixininterface.IItemEntityRenderState;

@Mixin(ItemEntityRenderState.class)
public class ItemEntityRenderStateMixin implements IItemEntityRenderState {
    @Unique
    private boolean silky$onGround;

    @Override
    public boolean silky$isOnGround() {
        return silky$onGround;
    }

    @Override
    public void silky$setOnGround(boolean onGround) {
        silky$onGround = onGround;
    }
}
