/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.api.v0.module;

import silky.client.config.SettingDef;
import silky.client.config.values.ConfigValue;
import silky.client.features.module.Module;

public interface ModuleExtensionContext {
    String addonId();

    String moduleId();

    Module module();

    void addSetting(SettingDef setting);

    ConfigValue<?> getConfigValue(String configName);

    boolean addModeOption(String configName, String option);
}
