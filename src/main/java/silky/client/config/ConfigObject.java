/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.config;

import silky.client.config.values.ConfigValue;

import java.util.List;

public interface ConfigObject {
    List<ConfigValue<?>> getConfigValues();
}
