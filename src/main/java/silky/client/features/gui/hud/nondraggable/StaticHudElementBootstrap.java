/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.features.gui.hud.nondraggable;

import silky.client.features.gui.hud.HudElementAutoLoader;

public enum StaticHudElementBootstrap {
    ;

    private static boolean initialized = false;

    public static void init() {
        if (initialized) return;
        initialized = true;

        HudElementAutoLoader.loadStatic("silky.client.features.gui.hud.nondraggable.impl");
    }
}
