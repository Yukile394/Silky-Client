/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.events.impl;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import silky.client.events.Event;

@Getter
public class EventBreakBlock extends Event {
    private final BlockPos pos;

    public EventBreakBlock(BlockPos pos) {
        this.pos = pos;
    }

}
