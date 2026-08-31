/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.util.sound.event;

import silky.client.events.Event;
import silky.client.util.sound.SoundRequest;

/** Cancellable event fired before a source or buffer is allocated. The request is mutable. */
public final class SoundPlayEvent extends Event {
    private final SoundRequest request;

    public SoundPlayEvent(SoundRequest request) {
        this.request = request;
    }

    public SoundRequest request() {
        return request;
    }
}
