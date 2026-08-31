/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.mixins.accessors;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PortalProcessor;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Entity.class)
public interface EntityAccessor {

    @Accessor("portalProcess")
    PortalProcessor silky$getPortalManager();

    @Accessor("stuckSpeedMultiplier")
    void silky$setMovementMultiplier(Vec3 multiplier);

    @Invoker("unsetRemoved")
    void silky$unsetRemoved();
}


