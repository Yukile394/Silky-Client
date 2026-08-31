/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.render.engine.rhi.resource;

import silky.client.render.engine.Texture;

public record GlyphAtlasPage(String id, Texture texture, int width, int height, boolean msdf) {
}
