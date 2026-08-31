/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.mixins;

import com.mojang.blaze3d.audio.DeviceList;
import com.mojang.blaze3d.audio.Library;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import silky.client.util.sound.SoundSystem;

/**
 * Hooks into Minecraft SoundEngine lifecycle to reset custom audio caches
 * when audio is (re)initialized or closed.
 */
@Mixin(Library.class)
public class LibraryMixin {
    @Inject(method = "init", at = @At("RETURN"))
    private void silky$resetOnInit(@Nullable String deviceSpecifier, DeviceList deviceList, boolean directionalAudio, CallbackInfo ci) {
        SoundSystem.get().reset();
    }

    @Inject(method = "cleanup", at = @At("HEAD"))
    private void silky$resetOnClose(CallbackInfo ci) {
        SoundSystem.get().reset();
    }
}

