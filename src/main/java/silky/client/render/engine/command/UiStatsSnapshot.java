/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.render.engine.command;

public record UiStatsSnapshot(long frameId,
                              int recordedCommands,
                              int shapeCommands,
                              int pathCommands,
                              int textureCommands,
                              int textCommands,
                              int itemCommands,
                              int effectCommands,
                              int compiledBatches,
                              int backendCommands) {
}
