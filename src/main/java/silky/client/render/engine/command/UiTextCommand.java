/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.render.engine.command;

public record UiTextCommand(String text, float x, float y, int argb, float size) implements UiCommand {
    @Override
    public UiCommandKind kind() {
        return UiCommandKind.TEXT;
    }
}
