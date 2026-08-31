/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.mixins.xaero.minimap;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import silky.client.compat.xaero.TriangulatorXaeroMinimapCompat;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.hud.minimap.element.render.MinimapElementRenderLocation;
import xaero.hud.minimap.waypoint.WaypointCollector;
import xaero.hud.minimap.waypoint.render.AbstractWaypointRenderContext;
import xaero.hud.minimap.waypoint.render.AbstractWaypointRenderProvider;
import xaero.hud.minimap.waypoint.render.WaypointMapRenderContext;
import xaero.hud.minimap.waypoint.render.world.WaypointWorldRenderContext;

import java.util.List;

@Pseudo
@Mixin(AbstractWaypointRenderProvider.class)
public abstract class AbstractWaypointRenderProviderMixin {

    @WrapOperation(
            method = "begin*",
            at = @At(
                    value = "INVOKE",
                    target = "Lxaero/hud/minimap/waypoint/WaypointCollector;collect(Ljava/util/List;)V"
            )
    )
    private void silky$addTriangulatorWaypoint(WaypointCollector instance,
                                                   List<Waypoint> destination,
                                                   Operation<Void> original,
                                                   MinimapElementRenderLocation location,
                                                   AbstractWaypointRenderContext context) {
        Waypoint waypoint = null;
        if (context instanceof WaypointWorldRenderContext) {
            waypoint = TriangulatorXaeroMinimapCompat.getHudWaypoint();
        } else if (context instanceof WaypointMapRenderContext) {
            waypoint = TriangulatorXaeroMinimapCompat.getMinimapWaypoint();
        }
        if (waypoint != null) {
            destination.add(waypoint);
        }
        original.call(instance, destination);
    }
}
