/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.render.sodium;

public record SodiumTerrainInteropStatsSnapshot(long terrainUpdatesScheduled,
                                                long rebuildsScheduled,
                                                long interopErrors) {
    public static final SodiumTerrainInteropStatsSnapshot EMPTY = new SodiumTerrainInteropStatsSnapshot(0, 0, 0);
}
