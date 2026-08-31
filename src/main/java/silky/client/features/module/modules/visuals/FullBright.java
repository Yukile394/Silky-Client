/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.features.module.modules.visuals;

import net.minecraft.util.Mth;
import silky.client.config.values.NumberValue;
import silky.client.events.EventHandler;
import silky.client.events.impl.LightmapModifyEvent;
import silky.client.features.module.Module;
import silky.client.features.module.ModuleCategory;
import silky.client.features.module.ModuleInfo;

//todo Description
@ModuleInfo(
        id = "fullbright",
        displayName = "FullBright",
        category = ModuleCategory.VISUALS
)
public class FullBright extends Module {

    private static final String SETTING_MIN_LIGHT = "min_light";
    private final NumberValue<Integer> minLight =
            num("fullBrightMinLight", SETTING_MIN_LIGHT, 15, 1, 15);

    public int getMinLight() {
        return isEnabled() ? minLight.get() : 0;
    }

    public float getMinLightStrength() {
        return isEnabled() ? Mth.clamp(minLight.get(), 1, 15) / 15.0f : 0.0f;
    }

    @EventHandler
    private void onLightmapState(LightmapModifyEvent event) {
        if (!isEnabled()) return;
        event.raiseMinimumLight(getMinLightStrength());
    }
}
