/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.addon;

import silky.client.api.v0.addon.SilkyAddonRuntimeContext;
import silky.client.api.v0.client.SilkyClientApi;

final class SilkyAddonRuntimeContextImpl implements SilkyAddonRuntimeContext {
    private final String addonId;

    SilkyAddonRuntimeContextImpl(String addonId) {
        this.addonId = addonId;
    }

    @Override
    public String addonId() {
        return addonId;
    }

    @Override
    public SilkyClientApi client() {
        return SilkyClientApi.get();
    }
}
