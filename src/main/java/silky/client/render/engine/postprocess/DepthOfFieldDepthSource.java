/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.render.engine.postprocess;

import silky.client.config.values.EnumValue;

public enum DepthOfFieldDepthSource implements EnumValue.IdProvider {
    WORLD_SCENE("world_scene"),
    MAIN("main"),
    PRE_TRANSLUCENT("pre_translucent"),
    OFF("off");

    private final String id;

    DepthOfFieldDepthSource(String id) {
        this.id = id;
    }

    @Override
    public String id() {
        return id;
    }
}
