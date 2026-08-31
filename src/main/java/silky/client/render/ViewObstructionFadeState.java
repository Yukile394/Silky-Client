/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.render;

public interface ViewObstructionFadeState {
    boolean silky$isViewObstructionFadeActive();

    float silky$getViewObstructionFadeAlpha();

    void silky$setViewObstructionFadeActive(boolean active);

    void silky$setViewObstructionFadeAlpha(float alpha);

    boolean silky$isSeeInvisibleFadeActive();

    void silky$setSeeInvisibleFadeActive(boolean active);
}
