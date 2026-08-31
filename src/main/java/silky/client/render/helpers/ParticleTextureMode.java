/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.render.helpers;

import silky.client.config.values.EnumValue;

public enum ParticleTextureMode implements EnumValue.IdProvider {
    BLOOM("bloom"),
    RANDOM("random");

    private final String id;

    ParticleTextureMode(String id) {
        this.id = id;
    }

    @Override
    public String id() {
        return id;
    }
}
