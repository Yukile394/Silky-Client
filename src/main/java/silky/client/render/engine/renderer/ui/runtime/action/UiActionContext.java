/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.render.engine.renderer.ui.runtime.action;

import silky.client.render.engine.renderer.ui.runtime.core.UiNode;

public record UiActionContext(UiNode node, UiActionRef ref, Object event) {
}
