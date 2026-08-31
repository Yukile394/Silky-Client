/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.features.module.modules.player;

import silky.client.config.values.BooleanValue;
import silky.client.features.module.Module;
import silky.client.features.module.ModuleCategory;
import silky.client.features.module.ModuleInfo;
import silky.client.features.module.Modules;
import silky.client.features.module.modules.combat.KillAura;

//todo Description
@ModuleInfo(
        id = "nointeract",
        displayName = "NoInteract",
        category = ModuleCategory.PLAYER
)
public final class NoInteract extends Module {
    private final BooleanValue onlyKillAura = bool("only_kill_aura", true);

    public boolean shouldBlockBlockInteraction() {
        KillAura killAura = Modules.get(KillAura.class);
        return shouldBlockBlockInteraction(
                isEnabled(),
                onlyKillAura.get(),
                killAura != null && killAura.isEnabled()
        );
    }

    static boolean shouldBlockBlockInteraction(
            boolean noInteractEnabled,
            boolean onlyKillAura,
            boolean killAuraEnabled
    ) {
        return noInteractEnabled && (!onlyKillAura || killAuraEnabled);
    }
}
