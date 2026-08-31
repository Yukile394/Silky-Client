/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.features.playeranimator;

/** Render-state extension carrying the solved rig from extraction into body and feature submits. */
public interface PlayerRigRenderState {
    PlayerRigInstance silky$getPlayerRig();

    void silky$setPlayerRig(PlayerRigInstance rig);
}
