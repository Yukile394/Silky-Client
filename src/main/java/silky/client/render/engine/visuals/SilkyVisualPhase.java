/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.render.engine.visuals;

/**
 * Render graph ordering for the custom world visual stack.
 */
public enum SilkyVisualPhase {
    PREPARE,
    SHADOWS,
    VOLUMETRICS,
    REFLECTIONS,
    COMPOSITE
}
