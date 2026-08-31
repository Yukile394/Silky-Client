/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.config.values;

public interface ColorValue {
    String get();

    void set(String value);

    int getArgb();

    boolean supportsAlpha();

    boolean isRainbow();

    String toRainbowValue();

    String rainbowFallbackHex();
}
