/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.render.engine.renderer.ui.runtime.core;

public interface UiLifecycle {
    default void onMount(UiNode node) {
    }

    default void onUpdate(UiNode node) {
    }

    default void onUnmount(UiNode node) {
    }
}
