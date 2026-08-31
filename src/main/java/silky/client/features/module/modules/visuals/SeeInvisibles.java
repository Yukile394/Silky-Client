/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.features.module.modules.visuals;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import silky.client.config.values.NumberValue;
import silky.client.features.module.Module;
import silky.client.features.module.ModuleCategory;
import silky.client.features.module.ModuleInfo;

//todo Description
@ModuleInfo(
        id = "seeinvisibles",
        displayName = "SeeInvisibles",
        category = ModuleCategory.VISUALS
)
public class SeeInvisibles extends Module {

    private final NumberValue<Integer> alpha = num("seeInvisiblesAlpha", "alpha", 110, 0, 255);

    public boolean shouldRenderInvisiblePlayer(LivingEntity entity) {
        return isEnabled() && entity instanceof Player && entity.isInvisible();
    }

    public boolean shouldRenderInvisiblePlayer(EntityType<?> entityType, boolean invisible) {
        return isEnabled() && invisible && entityType == net.minecraft.world.entity.EntityTypes.PLAYER;
    }

    public int getInvisiblePlayerTintArgb() {
        int alphaByte = Mth.clamp(alpha.get(), 0, 255);
        return (alphaByte << 24) | 0x00FFFFFF;
    }

    public float getInvisiblePlayerAlpha01() {
        return Mth.clamp(alpha.get(), 0, 255) / 255.0f;
    }
}
