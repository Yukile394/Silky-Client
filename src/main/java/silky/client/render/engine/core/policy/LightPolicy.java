/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.render.engine.core.policy;

/**
 * Placeholder for Silky lighting policy. Kept explicit so fullbright/ambient/shaderpack hooks do not leak into renderers.
 */
public interface LightPolicy {
    LightPolicy VANILLA = new LightPolicy() {
    };
}
