/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.render.engine.rhi.backend.gl;

import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.GpuDeviceBackend;
import com.mojang.blaze3d.systems.RenderSystem;
import org.jetbrains.annotations.Nullable;
import silky.client.mixininterface.IGlBackendInfo;
import silky.client.mixins.accessors.GpuDeviceAccessor;

public enum GlBackendAccess {
    ;

    public static @Nullable IGlBackendInfo current() {
        GpuDevice device = RenderSystem.tryGetDevice();
        if (!(device instanceof GpuDeviceAccessor accessor)) {
            return null;
        }
        GpuDeviceBackend backend = accessor.silky$getBackend();
        return backend instanceof IGlBackendInfo glBackend ? glBackend : null;
    }
}
