/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.render.engine.rhi.pipeline;

public enum FogPolicy {
    NONE,
    VANILLA,
    SILKY,
    SHADERPACK;

    public boolean requiresWorldFogUniform() {
        return this == VANILLA || this == SILKY || this == SHADERPACK;
    }
}
