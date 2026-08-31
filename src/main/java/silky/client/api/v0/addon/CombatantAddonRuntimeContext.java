/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.api.v0.addon;

import silky.client.api.v0.client.SilkyClientApi;

public interface SilkyAddonRuntimeContext {
    String addonId();

    SilkyClientApi client();
}
