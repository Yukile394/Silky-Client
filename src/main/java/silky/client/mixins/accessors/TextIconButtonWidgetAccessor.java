/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.mixins.accessors;

import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.WidgetSprites;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SpriteIconButton.class)
public interface TextIconButtonWidgetAccessor {
    @Accessor("spriteWidth")
    int silky$getTextureWidth();

    @Accessor("spriteHeight")
    int silky$getTextureHeight();

    @Accessor("sprite")
    WidgetSprites silky$getTexture();
}


