/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.mixins.accessors;

import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MultiPlayerGameMode.class)
public interface MultiPlayerGameModeAccessor {

    @Accessor("destroyBlockPos")
    BlockPos silky$getCurrentBreakingPos();

    @Accessor("destroyProgress")
    float silky$getCurrentBreakingProgress();

    @Accessor("destroyProgress")
    void silky$setCurrentBreakingProgress(float progress);

    @Accessor("destroyDelay")
    int silky$getBlockBreakingCooldown();

    @Accessor("destroyDelay")
    void silky$setBlockBreakingCooldown(int cooldown);

    @Accessor("carriedIndex")
    int silky$getLastSelectedSlot();

    @Accessor("carriedIndex")
    void silky$setLastSelectedSlot(int slot);

    @Invoker("ensureHasSentCarriedItem")
    void silky$syncSelectedSlot();
}
