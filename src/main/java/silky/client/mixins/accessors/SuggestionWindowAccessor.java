/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.mixins.accessors;

import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.renderer.Rect2i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(CommandSuggestions.SuggestionsList.class)
public interface SuggestionWindowAccessor {
    @Accessor("rect")
    Rect2i getSilky$area();

    @Accessor("suggestionList")
    List<?> getSilky$suggestions();

    @Accessor("current")
    int getSilky$selection();

    @Accessor("current")
    void setSilky$selection(int selection);

    @Accessor("offset")
    int getSilky$inWindowIndex();

    @Accessor("offset")
    void setSilky$inWindowIndex(int index);
}


