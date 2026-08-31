/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.mixins.sodium;

import com.llamalad7.mixinextras.sugar.Local;
import net.caffeinemc.mods.sodium.client.render.chunk.terrain.material.Material;
import net.caffeinemc.mods.sodium.client.render.chunk.vertex.format.ChunkVertexEncoder;
import net.caffeinemc.mods.sodium.client.render.model.MutableQuadViewImpl;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import silky.client.render.helpers.SodiumSurfaceFlagContext;
import silky.client.render.sodium.terrain.SilkyChunkVertexExtension;

@Pseudo
@Mixin(targets = "net.caffeinemc.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderer")
public abstract class SodiumBlockRendererMixin {

    @Inject(method = "renderModel", at = @At("HEAD"), remap = false)
    private void silky$pushSurfaceState(BlockStateModel model, BlockState state, BlockPos pos, BlockPos origin, CallbackInfo ci) {
        SodiumSurfaceFlagContext.pushForState(state, origin);
    }

    @Inject(method = "renderModel", at = @At("RETURN"), remap = false)
    private void silky$popSurfaceState(BlockStateModel model, BlockState state, BlockPos pos, BlockPos origin, CallbackInfo ci) {
        SodiumSurfaceFlagContext.pop();
    }

    @Inject(
            method = "bufferQuad",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/caffeinemc/mods/sodium/client/render/chunk/vertex/format/ChunkVertexEncoder$Vertex;light:I",
                    opcode = Opcodes.PUTFIELD,
                    shift = At.Shift.AFTER),
            remap = false
    )
    private void silky$writeSurfaceFlags(MutableQuadViewImpl quad, float[] brightness, Material material, CallbackInfo ci, @Local(ordinal = 0) ChunkVertexEncoder.Vertex vertex) {
        ((SilkyChunkVertexExtension) vertex).silky$setSurfaceFlags(SodiumSurfaceFlagContext.getSurfaceFlags(vertex.y));
    }
}
