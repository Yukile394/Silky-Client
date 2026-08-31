/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.config.common.impl;

import silky.client.config.common.CommonBooleanGroupSchema;
import silky.client.util.combat.protocol.CombatProtocolHeuristics;

import java.util.Map;

public final class CombatProtocolHeuristicSources implements CommonBooleanGroupSchema {
    @Override
    public String commonI18nKey() {
        return "combat.protocol_heuristics";
    }

    @Override
    public Map<String, Boolean> defaults() {
        return CombatProtocolHeuristics.defaultSourceToggles();
    }
}
