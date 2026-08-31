/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.render.engine.command;

public record UiEffectCommand(UiCommandKind kind, float x, float y, float width, float height,
                              float radius) implements UiCommand {
    public UiEffectCommand {
        if (kind != UiCommandKind.BLUR_REGION && kind != UiCommandKind.LIQUID_GLASS_REGION) {
            throw new IllegalArgumentException("UiEffectCommand kind must be a UI effect region");
        }
    }
}
