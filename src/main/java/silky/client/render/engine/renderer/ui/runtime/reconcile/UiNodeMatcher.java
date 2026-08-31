/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.render.engine.renderer.ui.runtime.reconcile;

import silky.client.render.engine.renderer.ui.runtime.core.UiNode;
import silky.client.render.engine.renderer.ui.runtime.core.UiNodeSpec;

public final class UiNodeMatcher {
    public boolean canReuse(UiNode node, UiNodeSpec spec) {
        return node != null && node.sameIdentity(spec);
    }
}
