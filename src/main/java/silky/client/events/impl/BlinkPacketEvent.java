/*
 * This file is part of the Silky Client distribution.
 * Silky modifications copyright (c) 2026 pivosos2007.
 *
 * Portions of this file are based on LiquidBounce
 * (https://github.com/CCBlueX/LiquidBounce).
 * Copyright (c) 2015-2026 CCBlueX.
 *
 * LiquidBounce portions are licensed under GPLv3-or-later.
 * Silky modifications are licensed under GPLv3.
 * See THIRD_PARTY_NOTICES.md for details.
 */

package silky.client.events.impl;

import lombok.Getter;
import net.minecraft.network.protocol.Packet;
import silky.client.events.Event;
import silky.client.util.network.BlinkManager;
import silky.client.util.network.TransferOrigin;

/**
 * Adapted from LiquidBounce's BlinkPacketEvent.
 */
public class BlinkPacketEvent extends Event {
    private final Packet<?> packet;
    @Getter
    private final TransferOrigin origin;
    @Getter
    private BlinkManager.Action action = BlinkManager.Action.FLUSH;

    public BlinkPacketEvent(Packet<?> packet, TransferOrigin origin) {
        this.packet = packet;
        this.origin = origin;
    }

    @SuppressWarnings("unchecked")
    public <T extends Packet<?>> T getPacket() {
        return (T) packet;
    }

    public void setAction(BlinkManager.Action action) {
        if (action == null || this.action == action || this.action.getPriority() >= action.getPriority()) {
            return;
        }
        this.action = action;
    }
}
