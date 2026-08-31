/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.mixins.iris;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.irisshaders.iris.Iris;
import net.irisshaders.iris.shaderpack.include.AbsolutePackPath;
import net.irisshaders.iris.shaderpack.include.IncludeProcessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import silky.client.render.iris.patch.ShaderPatchEngine;
import silky.client.util.logging.DebugLog;

import java.util.Objects;

@Pseudo
@Mixin(value = IncludeProcessor.class, remap = false)
public abstract class IrisIncludeProcessorMixin {
    @Unique
    private ShaderPatchEngine.Session silky$patchSession;
    @Unique
    private String silky$patchSessionPackName;

    @Unique
    private static String silky$currentShaderPackName() {
        String loadingPackName = ShaderPatchEngine.loadingShaderPackName();
        if (loadingPackName != null && !loadingPackName.isBlank()) {
            return loadingPackName;
        }
        try {
            return Iris.getCurrentPackName();
        } catch (Throwable throwable) {
            DebugLog.warn("[IrisPatch] Unable to query current Iris shaderpack name: %s", throwable.getClass().getSimpleName());
            return null;
        }
    }

    @ModifyReturnValue(method = "getIncludedFile", at = @At("RETURN"), remap = false)
    private ImmutableList<String> silky$compileShaderpackPatch(ImmutableList<String> lines, AbsolutePackPath path) {
        if (lines == null || path == null) {
            return lines;
        }

        String packName = silky$currentShaderPackName();
        if (silky$patchSession == null || !Objects.equals(packName, silky$patchSessionPackName)) {
            silky$patchSessionPackName = packName;
            silky$patchSession = ShaderPatchEngine.newSession(packName);
            DebugLog.info("[IrisPatch] IncludeProcessor hook active: shaderPack='%s' active=%s firstPath=%s",
                    packName == null ? "" : packName,
                    silky$patchSession.isActive(),
                    path.getPathString());
        }

        if (!silky$patchSession.isActive()) {
            return lines;
        }

        IncludeProcessor processor = (IncludeProcessor) (Object) this;
        return silky$patchSession.patch(
                path.getPathString(),
                lines,
                candidate -> processor.getIncludedFile(AbsolutePackPath.fromAbsolutePath(candidate))
        );
    }
}
