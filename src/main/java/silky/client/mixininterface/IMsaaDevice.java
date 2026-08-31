/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.mixininterface;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.textures.GpuTexture;

@Deprecated
public interface IMsaaDevice {
    GpuTexture silky$createMsaaTexture(String label,
                                           int usage,
                                           GpuFormat format,
                                           int width,
                                           int height,
                                           int samples);
}
