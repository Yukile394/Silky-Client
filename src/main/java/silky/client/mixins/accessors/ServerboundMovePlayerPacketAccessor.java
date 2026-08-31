/*
 * This file is part of the Silky Client distribution.
 * Silky modifications copyright (c) 2026 pivosos2007.
 *
 * Portions of this file are based on, adapted from, or implemented
 * with reference to Meteor Client
 * (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 *
 * Licensed under the GNU General Public License v3.0.
 * See THIRD_PARTY_NOTICES.md for details.
 */

package silky.client.mixins.accessors;

import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerboundMovePlayerPacket.class)
public interface ServerboundMovePlayerPacketAccessor {
    @Mutable
    @Accessor("x")
    void silky$setX(double x);

    @Mutable
    @Accessor("y")
    void silky$setY(double y);

    @Mutable
    @Accessor("z")
    void silky$setZ(double z);

    @Mutable
    @Accessor("onGround")
    void silky$setOnGround(boolean onGround);
}
