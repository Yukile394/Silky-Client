/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.mixins.accessors;

import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Level.class)
public interface WorldAccessor {

    @Accessor("isClientSide")
    boolean silky$isClient();

    @Accessor("rainLevel")
    float silky$getRainGradientRaw();

    @Accessor("oRainLevel")
    float silky$getLastRainGradientRaw();

    @Accessor("thunderLevel")
    float silky$getThunderGradientRaw();

    @Accessor("oThunderLevel")
    float silky$getLastThunderGradientRaw();
}


