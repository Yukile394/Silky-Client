/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.render.engine.command;

import silky.client.render.engine.renderer.ui.draw.UiPaint;
import silky.client.render.engine.renderer.ui.draw.UiShape;
import silky.client.render.engine.renderer.ui.draw.UiStroke;

public record UiShapeCommand(UiShape shape, UiPaint paint, UiStroke stroke, boolean fill) implements UiCommand {
    @Override
    public UiCommandKind kind() {
        return UiCommandKind.SHAPE;
    }
}
