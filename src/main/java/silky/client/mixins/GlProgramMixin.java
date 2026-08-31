/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.opengl.GlProgram;
import com.mojang.blaze3d.opengl.GlShaderModule;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import silky.client.util.logging.DebugLog;

@Mixin(GlProgram.class)
public abstract class GlProgramMixin {
    @Unique
    private static final String silky$namespace = "silky";

    @WrapOperation(
            method = "link",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/slf4j/Logger;info(Ljava/lang/String;[Ljava/lang/Object;)V"
            )
    )
    private static void silky$wrapProgramLinkInfo(Logger logger,
                                                       String format,
                                                       Object[] args,
                                                       Operation<Void> original,
                                                       GlShaderModule vertexShader,
                                                       GlShaderModule fragmentShader,
                                                       VertexFormat[] vertexFormats,
                                                       String debugLabel) {
        if (silky$isSilkyShader(vertexShader)
                || silky$isSilkyShader(fragmentShader)
                || silky$isSilkyLabel(debugLabel)) {
            DebugLog.renderThread("[RenderPipeline] %s", silky$formatSlf4j(format, args));
            return;
        }

        original.call(logger, format, args);
    }

    @WrapOperation(
            method = "setupBindGroupLayouts",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"
            )
    )
    private void silky$wrapPipelineWarning(Logger logger,
                                                 String format,
                                                 Object first,
                                                 Object second,
                                                 Operation<Void> original) {
        String debugLabel = ((GlProgram) (Object) this).getDebugLabel();
        if (silky$isSilkyLabel(debugLabel)) {
            DebugLog.warn("[RenderPipeline] %s", silky$formatSlf4j(format, first, second));
            return;
        }

        original.call(logger, format, first, second);
    }

    @Unique
    private static boolean silky$isSilkyShader(GlShaderModule shader) {
        return shader != null && silky$isSilkyId(shader.getId());
    }

    @Unique
    private static boolean silky$isSilkyId(Identifier id) {
        return id != null && silky$namespace.equals(id.getNamespace());
    }

    @Unique
    private static boolean silky$isSilkyLabel(String label) {
        if (label == null || label.isEmpty()) {
            return false;
        }
        return label.startsWith(silky$namespace + ":") || label.startsWith("Silky");
    }

    @Unique
    private static String silky$formatSlf4j(String format, Object... args) {
        String template = String.valueOf(format);
        if (args == null || args.length == 0) {
            return template;
        }

        StringBuilder out = new StringBuilder(template.length() + args.length * 16);
        int cursor = 0;
        int argIndex = 0;
        while (argIndex < args.length) {
            int placeholder = template.indexOf("{}", cursor);
            if (placeholder < 0) {
                break;
            }
            out.append(template, cursor, placeholder);
            out.append(String.valueOf(args[argIndex++]));
            cursor = placeholder + 2;
        }
        out.append(template, cursor, template.length());
        while (argIndex < args.length) {
            out.append(' ').append(String.valueOf(args[argIndex++]));
        }
        return out.toString();
    }
}
