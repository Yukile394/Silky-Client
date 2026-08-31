/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.api.v0.client;

import silky.client.features.module.Module;
import silky.client.features.module.ModuleManager;

import java.util.List;

public final class SilkyClientApi {
    private static final SilkyClientApi INSTANCE = new SilkyClientApi();

    private SilkyClientApi() {
    }

    public static SilkyClientApi get() {
        return INSTANCE;
    }

    public List<Module> modules() {
        return ModuleManager.getModules();
    }

    public Module module(String id) {
        return ModuleManager.get(id);
    }

    public boolean isModuleEnabled(String id) {
        return ModuleManager.isEnabled(id);
    }

    public void setModuleEnabled(String id, boolean enabled) {
        ModuleManager.setEnabled(id, enabled);
    }
}
