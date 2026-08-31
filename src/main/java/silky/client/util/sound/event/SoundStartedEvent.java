/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.util.sound.event;

import silky.client.events.Event;
import silky.client.util.sound.SoundDefinition;
import silky.client.util.sound.SoundInstance;

public final class SoundStartedEvent extends Event {
    private final SoundDefinition definition;
    private final SoundInstance instance;

    public SoundStartedEvent(SoundDefinition definition, SoundInstance instance) {
        this.definition = definition;
        this.instance = instance;
    }

    public SoundDefinition definition() { return definition; }
    public SoundInstance instance() { return instance; }
}
