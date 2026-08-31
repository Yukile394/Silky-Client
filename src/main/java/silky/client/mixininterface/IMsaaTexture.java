/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.mixininterface;

public interface IMsaaTexture {
    void silky$setSamples(int samples);

    int silky$getSamples();

    boolean silky$isMsaa();

    int silky$getGlId();
}
