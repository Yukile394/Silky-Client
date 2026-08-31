/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.api.v0.clickgui;

import silky.client.render.engine.renderer.Renderer2D;
import silky.client.render.engine.text.TextRenderer;

public record SilkyClickGuiRenderContext(
        Renderer2D renderer,
        TextRenderer regularFont,
        TextRenderer mediumFont
) {
}
