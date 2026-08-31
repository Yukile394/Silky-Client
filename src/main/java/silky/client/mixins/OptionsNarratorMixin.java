/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.NarratorStatus;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import silky.client.util.NarratorBlocker;

import java.io.File;

@Mixin(Options.class)
public abstract class OptionsNarratorMixin {

    @Inject(method = "<init>(Lnet/minecraft/client/Minecraft;Ljava/io/File;)V", at = @At("RETURN"))
    private void silky$enforceNarratorDefaults(Minecraft client, File optionsFile, CallbackInfo ci) {
        NarratorBlocker.enforce((Options) (Object) this);
    }

    @Inject(method = "save", at = @At("HEAD"))
    private void silky$enforceNarratorBeforeWrite(CallbackInfo ci) {
        NarratorBlocker.enforce((Options) (Object) this);
    }

    @Inject(method = "narrator", at = @At("HEAD"))
    private void silky$enforceNarratorGetter(CallbackInfoReturnable<OptionInstance<NarratorStatus>> cir) {
        NarratorBlocker.enforce((Options) (Object) this);
    }

    @Inject(method = "narratorHotkey", at = @At("HEAD"))
    private void silky$enforceNarratorHotkeyGetter(CallbackInfoReturnable<OptionInstance<Boolean>> cir) {
        NarratorBlocker.enforce((Options) (Object) this);
    }
}
