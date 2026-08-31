/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.mixins.xaero.worldmap;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import silky.client.compat.xaero.TriangulatorXaeroWorldMapCompat;
import xaero.map.element.render.ElementRenderLocation;
import xaero.map.mods.SupportXaeroMinimap;
import xaero.map.mods.gui.Waypoint;
import xaero.map.mods.gui.WaypointRenderContext;
import xaero.map.mods.gui.WaypointRenderProvider;

import java.util.ArrayDeque;
import java.util.Queue;

@Pseudo
@Mixin(WaypointRenderProvider.class)
public final class WaypointRenderProviderMixin {

    @Shadow
    @Final
    private SupportXaeroMinimap minimap;

    @Unique
    private Queue<Waypoint> silky$pendingWaypoints;

    @Unique
    private boolean silky$originalHadNext;

    @Inject(method = "begin*", at = @At("HEAD"))
    private void silky$begin(ElementRenderLocation location,
                                 WaypointRenderContext context,
                                 CallbackInfo ci) {
        silky$pendingWaypoints = new ArrayDeque<>();
        Waypoint triangulatorWaypoint = TriangulatorXaeroWorldMapCompat.getWaypoint(minimap);
        if (triangulatorWaypoint != null) {
            silky$pendingWaypoints.add(triangulatorWaypoint);
        }
        silky$originalHadNext = false;
    }

    @Inject(method = "hasNext*", at = @At("RETURN"), cancellable = true)
    private void silky$hasNext(ElementRenderLocation location,
                                   WaypointRenderContext context,
                                   CallbackInfoReturnable<Boolean> cir) {
        silky$originalHadNext = cir.getReturnValue();
        cir.setReturnValue(silky$originalHadNext
                || (silky$pendingWaypoints != null && !silky$pendingWaypoints.isEmpty()));
    }

    @Inject(method = "getNext*", at = @At("HEAD"), cancellable = true)
    private void silky$getNext(ElementRenderLocation location,
                                   WaypointRenderContext context,
                                   CallbackInfoReturnable<Waypoint> cir) {
        if (silky$originalHadNext) {
            silky$originalHadNext = false;
            return;
        }
        if (silky$pendingWaypoints != null && !silky$pendingWaypoints.isEmpty()) {
            cir.setReturnValue(silky$pendingWaypoints.poll());
        }
    }

    @Inject(method = "end*", at = @At("HEAD"))
    private void silky$end(ElementRenderLocation location,
                               WaypointRenderContext context,
                               CallbackInfo ci) {
        silky$pendingWaypoints = null;
        silky$originalHadNext = false;
    }
}
