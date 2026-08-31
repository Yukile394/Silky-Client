/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.api.v0.addon;

/**
 * Fabric entrypoint contract for Silky addons.
 *
 * <p>Declare it in fabric.mod.json under the {@value #ENTRYPOINT_KEY} entrypoint.
 */
public interface SilkyAddon {
    String ENTRYPOINT_KEY = "silky:addon";
    int API_VERSION = 0;

    /**
     * Called before Silky auto-loads its built-in modules.
     *
     * <p>Use this hook only for startup module exclusions. Normal addon registration
     * still belongs in {@link #onInitialize(SilkyAddonContext)}.</p>
     */
    default void onConfigureModules(SilkyModuleLoadContext context) {
    }

    void onInitialize(SilkyAddonContext context);

    /**
     * Called after all built-in and addon module configs have been loaded and applied.
     * This is the safe startup hook for mutating settings of existing modules.
     */
    default void onClientReady(SilkyAddonRuntimeContext context) {
    }

    default void onRuntimeSuspended(SilkyAddonRuntimeContext context) {
    }

    default void onRuntimeResumed(SilkyAddonRuntimeContext context) {
    }

    default void onShutdown(SilkyAddonRuntimeContext context) {
    }
}
