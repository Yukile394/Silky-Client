/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.mixins.sodium;

import net.caffeinemc.mods.sodium.client.render.chunk.vertex.format.ChunkVertexEncoder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import silky.client.render.sodium.terrain.SilkyChunkVertexExtension;

@Mixin(ChunkVertexEncoder.Vertex.class)
public abstract class SodiumChunkVertexMixin implements SilkyChunkVertexExtension {
    @Unique
    private int silky$surfaceFlags;

    @Inject(method = "copyVertexTo", at = @At("HEAD"))
    private static void silky$copyVertexData(ChunkVertexEncoder.Vertex from, ChunkVertexEncoder.Vertex to, CallbackInfo ci) {
        ((SilkyChunkVertexExtension) from).silky$copyData((SilkyChunkVertexExtension) to);
    }

    @Override
    public void silky$setSurfaceFlags(int surfaceFlags) {
        this.silky$surfaceFlags = surfaceFlags;
    }

    @Override
    public int silky$getSurfaceFlags() {
        return this.silky$surfaceFlags;
    }

    @Override
    public void silky$copyData(SilkyChunkVertexExtension dest) {
        dest.silky$setSurfaceFlags(this.silky$surfaceFlags);
    }
}
