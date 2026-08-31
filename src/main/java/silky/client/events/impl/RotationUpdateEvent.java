/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.events.impl;

import silky.client.events.Event;
import lombok.Getter;

/**
 * Rotation lifecycle event fired each tick in two phases.
 */
@Getter
public final class RotationUpdateEvent extends Event {
    private final Type type;

    public RotationUpdateEvent(Type type) {
        this.type = type == null ? Type.PRE : type;
    }

    public enum Type {
        PRE,
        POST
    }
}
