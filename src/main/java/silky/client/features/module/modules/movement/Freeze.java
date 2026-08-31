/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.features.module.modules.movement;

import silky.client.features.module.Module;
import silky.client.features.module.ModuleCategory;
import silky.client.features.module.ModuleInfo;

//todo Description
@ModuleInfo(
        id = "freeze",
        displayName = "Freeze",
        category = ModuleCategory.MOVEMENT
)
public final class Freeze extends Module {

    public Freeze() {
        setDefaultBind("COMMA");
    }
}
