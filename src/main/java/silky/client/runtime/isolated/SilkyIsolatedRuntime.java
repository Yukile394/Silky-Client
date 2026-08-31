/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.runtime.isolated;

import silky.client.events.UsedImplicitly;
import silky.client.runtime.annotation.RuntimeAssertion;
import silky.client.runtime.annotation.RuntimeAssertionPhase;
import silky.client.runtime.annotation.RuntimeResume;
import silky.client.runtime.annotation.RuntimeSuspend;

@UsedImplicitly
public final class SilkyIsolatedRuntime {
    private boolean active = true;

    @UsedImplicitly
    @RuntimeSuspend
    private void suspend() {
        active = false;
    }

    @UsedImplicitly
    @RuntimeResume
    private void resume() {
        active = true;
    }

    @UsedImplicitly
    @RuntimeAssertion(phase = RuntimeAssertionPhase.SUSPENDED)
    private boolean assertSuspended() {
        return !active;
    }

    @UsedImplicitly
    @RuntimeAssertion(phase = RuntimeAssertionPhase.ACTIVE)
    private boolean assertActive() {
        return active;
    }
}
