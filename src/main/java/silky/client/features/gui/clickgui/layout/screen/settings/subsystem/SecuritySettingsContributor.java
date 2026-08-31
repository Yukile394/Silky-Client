/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.features.gui.clickgui.layout.screen.settings.subsystem;

import silky.client.config.subsystem.SecurityConfig;
import silky.client.config.SettingDef;
import silky.client.config.SettingOwner;

import java.util.List;

public final class SecuritySettingsContributor implements MainSettingsContributor {
    @Override public String id() { return "security"; }
    @Override public String titleKey() { return "clickgui.settings.main.security"; }
    @Override public String fallbackTitle() { return "Security"; }
    @Override public String icon() { return "shield-user"; }
    @Override public int order() { return 200; }
    @Override public SettingOwner owner() { return SecurityConfig.get(); }
    @Override public List<SettingDef> settingDefs() { return SecurityConfig.get().getSettingDefs(); }
}
