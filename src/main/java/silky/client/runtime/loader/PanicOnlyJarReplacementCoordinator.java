/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.runtime.loader;

public final class PanicOnlyJarReplacementCoordinator implements JarReplacementCoordinator {
    private volatile boolean restartRequired;

    @Override
    public boolean canPrepareReplacement() {
        return true;
    }

    @Override
    public void prepareReplacement(String reason) {
        restartRequired = true;
    }

    @Override
    public boolean requiresRestart() {
        return restartRequired;
    }
}
