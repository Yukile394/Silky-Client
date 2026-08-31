/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.runtime.loader;

import silky.client.runtime.RuntimeGate;

public final class RuntimeGateSourcePolicy implements RuntimeSourcePolicy {
    @Override
    public boolean mayLoadClientClass(String className) {
        return !RuntimeGate.isJarReplacementMode();
    }

    @Override
    public boolean mayLoadClientResource(String resourcePath) {
        return !RuntimeGate.isJarReplacementMode();
    }

    @Override
    public boolean isJarReplacementLocked() {
        return RuntimeGate.isJarReplacementMode();
    }
}
