/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.mixins;

import com.mojang.blaze3d.opengl.DirectStateAccess;
import com.mojang.blaze3d.opengl.FrameBufferCache;
import com.mojang.blaze3d.opengl.GlRenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.shaders.ShaderSource;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import silky.client.util.logging.DebugLog;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import silky.client.render.engine.profiler.ProfilerPhase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.gen.Invoker;
import silky.client.mixininterface.IGlBackendInfo;

@Mixin(targets = "com.mojang.blaze3d.opengl.GlDevice")
public abstract class GlDeviceBackendMixin implements IGlBackendInfo {
    @Override
    @Invoker("directStateAccess")
    public abstract DirectStateAccess silky$directStateAccess();

    @Override
    @Invoker("frameBufferCache")
    public abstract FrameBufferCache silky$frameBufferCache();
    @Unique
    private ProfilerPhase.Scope silky$pipelineCompileScope;


    @WrapOperation(
            method = {"compileShader", "compileProgram"},
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"
            )
    )
    private void silky$wrapSilkyCompileError(Logger logger,
                                                       String format,
                                                       Object first,
                                                       Object second,
                                                       Operation<Void> original) {
        if (silky$containsSilkyId(first) || silky$containsSilkyId(second)) {
            DebugLog.error("[RenderPipeline] " + silky$formatSlf4j(format, first, second));
            return;
        }

        original.call(logger, format, first, second);
    }

    @WrapOperation(
            method = "compileShader",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/slf4j/Logger;error(Ljava/lang/String;[Ljava/lang/Object;)V"
            )
    )
    private void silky$wrapSilkyShaderCompileError(Logger logger,
                                                             String format,
                                                             Object[] args,
                                                             Operation<Void> original) {
        if (silky$containsSilkyId(args)) {
            DebugLog.error("[RenderPipeline] " + silky$formatSlf4j(format, args));
            return;
        }

        original.call(logger, format, args);
    }

    @Unique
    private static boolean silky$containsSilkyId(Object value) {
        if (value instanceof Identifier id) {
            return "silky".equals(id.getNamespace());
        }
        if (value instanceof Object[] values) {
            for (Object element : values) {
                if (silky$containsSilkyId(element)) {
                    return true;
                }
            }
        }
        return false;
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

    @Inject(method = "compilePipeline", at = @At("HEAD"))
    private void silky$profilePipelineCompileHead(RenderPipeline pipeline, ShaderSource shaderSource, CallbackInfoReturnable<GlRenderPipeline> cir) {
        ProfilerPhase.Scope staleScope = silky$pipelineCompileScope;
        silky$pipelineCompileScope = null;
        if (staleScope != null) staleScope.close();

        if (ProfilerPhase.isActive()) {
            silky$pipelineCompileScope = ProfilerPhase.scope("gl:compile_pipeline");
        }
    }

    @Inject(method = "compilePipeline", at = @At("RETURN"))
    private void silky$profilePipelineCompileReturn(RenderPipeline pipeline, ShaderSource shaderSource, CallbackInfoReturnable<GlRenderPipeline> cir) {
        ProfilerPhase.Scope scope = silky$pipelineCompileScope;
        silky$pipelineCompileScope = null;
        if (scope != null) scope.close();
    }

}
