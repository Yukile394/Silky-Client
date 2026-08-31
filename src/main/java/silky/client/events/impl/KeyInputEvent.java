/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.events.impl;

import lombok.Getter;
import net.minecraft.client.input.KeyEvent;
import silky.client.events.Event;

@Getter
public final class KeyInputEvent extends Event {
    private final int action;
    private final KeyEvent input;

    public KeyInputEvent(int action, KeyEvent input) {
        this.action = action;
        this.input = input;
    }

}
