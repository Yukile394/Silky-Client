/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.mixins;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import silky.client.render.ViewObstructionFadeState;
import silky.client.features.playeranimator.PlayerRigInstance;
import silky.client.features.playeranimator.PlayerRigRenderState;

@Mixin(LivingEntityRenderState.class)
public abstract class LivingEntityRenderStateMixin implements ViewObstructionFadeState, PlayerRigRenderState {
    @Unique
    private boolean silky$viewObstructionFadeActive;

    @Unique
    private float silky$viewObstructionFadeAlpha = 1.0f;

    @Unique
    private boolean silky$seeInvisibleFadeActive;

    @Unique
    private PlayerRigInstance silky$playerRig;

    @Override
    public boolean silky$isViewObstructionFadeActive() {
        return silky$viewObstructionFadeActive;
    }

    @Override
    public float silky$getViewObstructionFadeAlpha() {
        return silky$viewObstructionFadeAlpha;
    }

    @Override
    public void silky$setViewObstructionFadeActive(boolean active) {
        this.silky$viewObstructionFadeActive = active;
    }

    @Override
    public void silky$setViewObstructionFadeAlpha(float alpha) {
        this.silky$viewObstructionFadeAlpha = alpha;
    }

    @Override
    public boolean silky$isSeeInvisibleFadeActive() {
        return silky$seeInvisibleFadeActive;
    }

    @Override
    public void silky$setSeeInvisibleFadeActive(boolean active) {
        this.silky$seeInvisibleFadeActive = active;
    }

    @Override
    public PlayerRigInstance silky$getPlayerRig() {
        return silky$playerRig;
    }

    @Override
    public void silky$setPlayerRig(PlayerRigInstance rig) {
        this.silky$playerRig = rig;
    }
}
