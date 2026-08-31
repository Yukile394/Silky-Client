/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.render.engine.renderer.ui.runtime.script;

import silky.client.render.engine.renderer.ui.runtime.core.UiProps;

public record UiScriptRenderContext(
        long frame,
        double time,
        double delta,
        float width,
        float height,
        UiProps props
) {
    public UiScriptRenderContext {
        props = props != null ? props : UiProps.EMPTY;
    }
}
