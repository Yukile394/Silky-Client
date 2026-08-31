/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.api.v0.render;

@FunctionalInterface
public interface SilkyPostProcessCallback {
    boolean render(SilkyPostProcessContext context);
}
