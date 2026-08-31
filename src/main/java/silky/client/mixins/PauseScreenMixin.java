/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.mixins;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.PauseScreen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import silky.client.features.module.Modules;
import silky.client.features.module.modules.combat.PvpCooldowns;

@Mixin(PauseScreen.class)
public abstract class PauseScreenMixin {
    @Shadow
    @Nullable
    private Button disconnectButton;

    @Unique
    private Button silky$lastExitButton;
    @Unique
    private boolean silky$disconnectBlocked;
    @Unique
    private boolean silky$previousExitActive = true;

    @Inject(method = "init", at = @At("TAIL"))
    private void silky$blockDisconnectOnInit(CallbackInfo ci) {
        silky$syncDisconnectButton();
    }

    @Inject(method = "extractRenderState", at = @At("HEAD"))
    private void silky$blockDisconnectOnRender(GuiGraphicsExtractor context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        silky$syncDisconnectButton();
    }

    @Unique
    private void silky$syncDisconnectButton() {
        Button button = disconnectButton;
        if (button == null) {
            silky$lastExitButton = null;
            silky$disconnectBlocked = false;
            return;
        }

        if (button != silky$lastExitButton) {
            silky$lastExitButton = button;
            silky$disconnectBlocked = false;
            silky$previousExitActive = button.active;
        }

        PvpCooldowns cooldowns = Modules.get(PvpCooldowns.class);
        boolean shouldBlock = cooldowns != null && cooldowns.shouldBlockDisconnect();
        if (shouldBlock) {
            if (!silky$disconnectBlocked) {
                silky$previousExitActive = button.active;
            }
            silky$disconnectBlocked = true;
            button.active = false;
            button.setTooltip(Tooltip.create(cooldowns.getDisconnectBlockTooltip()));
            return;
        }

        if (silky$disconnectBlocked) {
            button.active = silky$previousExitActive;
            button.setTooltip(null);
            silky$disconnectBlocked = false;
        }
    }
}
