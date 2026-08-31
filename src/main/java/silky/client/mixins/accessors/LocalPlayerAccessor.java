/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.mixins.accessors;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LocalPlayer.class)
public interface LocalPlayerAccessor {

    @Accessor("minecraft")
    Minecraft getClient();

    @Accessor("portalEffectIntensity")
    float silky$getNauseaIntensity();

    @Accessor("portalEffectIntensity")
    void silky$setNauseaIntensity(float value);

    @Accessor("oPortalEffectIntensity")
    float silky$getLastNauseaIntensity();

    @Accessor("oPortalEffectIntensity")
    void silky$setLastNauseaIntensity(float value);

    @Accessor("positionReminder")
    void silky$setTicksSinceLastPositionPacketSent(int ticks);

    @Accessor("wasSprinting")
    void silky$setLastSprinting(boolean sprinting);

    @Invoker("canStartSprinting")
    boolean silky$canStartSprinting();

    @Invoker("shouldStopRunSprinting")
    boolean silky$shouldStopSprinting();

    @Invoker("shouldStopSwimSprinting")
    boolean silky$shouldStopSwimSprinting();
}


