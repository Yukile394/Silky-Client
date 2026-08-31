/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.features.module.modules.misc;

import silky.client.config.values.BooleanValue;
import silky.client.features.module.Module;
import silky.client.features.module.ModuleCategory;
import silky.client.features.module.ModuleInfo;
import silky.client.runtime.RuntimeGate;

//todo Description
@ModuleInfo(
        id = "betterminecraft",
        displayName = "BetterMinecraft",
        category = ModuleCategory.MISC
)
public final class BetterMinecraft extends Module {

    private final BooleanValue hideRecipeBook = bool("hide_recipe_book", true);
    private final BooleanValue tablistRelations = bool("tablist_relations", true);

    public boolean isHideRecipeBookEnabled() {
        return !RuntimeGate.isPanic() && isEnabled() && hideRecipeBook.get();
    }

    public boolean isTablistRelationsEnabled() {
        return !RuntimeGate.isPanic() && isEnabled() && tablistRelations.get();
    }
}
