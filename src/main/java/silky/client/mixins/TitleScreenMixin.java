/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.mixins;

import silky.client.config.MainConfig;
import silky.client.util.screen.ClientScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import silky.client.features.gui.mainmenu.SilkyMainMenuScreen;
import silky.client.runtime.RuntimeGate;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin {

    @Inject(method = "init", at = @At("HEAD"), cancellable = true)
    private void silky$replaceTitleScreen(CallbackInfo ci) {
        if (RuntimeGate.isPanic()) return;
        if (!MainConfig.get().isSilkyMainMenuEnabled()) return;
        if (SilkyMainMenuScreen.shouldUseVanillaTitleScreen()) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) return;
        if (ClientScreen.current() instanceof SilkyMainMenuScreen) return;
        ClientScreen.show(mc, new SilkyMainMenuScreen());
        ci.cancel();
    }
}




