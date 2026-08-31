/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.render.engine.command;

public record UiPrimitiveCommand(float x, float y, float width, float height, int argb) implements UiCommand {
    @Override
    public UiCommandKind kind() {
        return UiCommandKind.PRIMITIVE;
    }
}
