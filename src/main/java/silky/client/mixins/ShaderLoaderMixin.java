/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.mixins;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.blaze3d.shaders.ShaderType;
import net.minecraft.client.renderer.PostChainConfig;
import net.minecraft.client.renderer.ShaderManager;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import silky.client.mixins.accessors.ShaderLoaderDefinitionsAccessor;
import silky.client.mixins.accessors.ShaderSourceKeyAccessor;
import silky.client.render.engine.shader.SilkyShaderSources;
import silky.client.render.iris.IrisCompatibilityGuards;
import silky.client.util.logging.DebugLog;
import silky.client.util.resources.RenderResourceReadiness;
import silky.client.util.resources.ResourceReloadHooks;

import java.lang.reflect.Constructor;
import java.util.LinkedHashMap;
import java.util.Map;

@Mixin(ShaderManager.class)
public abstract class ShaderLoaderMixin {
    @Unique
    private static final Identifier SILKY_ENTITY_VERTEX = Identifier.fromNamespaceAndPath("silky", "shaders/core/entity_dither.vsh");
    @Unique
    private static final Identifier SILKY_ENTITY_FRAGMENT = Identifier.fromNamespaceAndPath("silky", "shaders/core/entity_dither.fsh");
    @Unique
    private static final Identifier VANILLA_ENTITY_ID = Identifier.withDefaultNamespace("core/entity");
    @Unique
    private static boolean silky$loggedEntityOverride;
    @Unique
    private static boolean silky$loggedExtendedShaderSources;
    @Unique
    private static Constructor<?> silky$shaderSourceKeyConstructor;

    @Unique
    private static boolean silky$addExtendedShaderSources(ResourceManager resourceManager, Map<Object, String> shaderSources) {
        Map<Identifier, Resource> resources = resourceManager.listResources("shaders", SilkyShaderSources::isSilkyExtendedSource);
        if (resources.isEmpty()) {
            return false;
        }

        int added = 0;
        for (Map.Entry<Identifier, Resource> entry : resources.entrySet()) {
            Identifier resourceId = entry.getKey();
            ShaderType type = SilkyShaderSources.typeByLocation(resourceId);
            if (type == null) continue;

            String source = SilkyShaderSources.load(resourceManager, resourceId, entry.getValue());
            shaderSources.put(silky$newShaderSourceKey(resourceId, type), source);
            added++;

            Identifier canonicalId = SilkyShaderSources.canonicalShaderId(resourceId);
            if (!canonicalId.equals(resourceId)) {
                shaderSources.put(silky$newShaderSourceKey(canonicalId, type), source);
                added++;
            }
        }

        if (added > 0 && !silky$loggedExtendedShaderSources) {
            silky$loggedExtendedShaderSources = true;
            DebugLog.renderThread("[ShaderSource] registered Silky .vert/.frag sources in ShaderManager: " + added + " keys");
        }
        return added > 0;
    }

    @Unique
    private static boolean silky$allowVanillaEntityShaderOverride() {
        if (IrisCompatibilityGuards.suppressSilkyTerrainShaderOverrides()) {
            return false;
        }

        return true;
    }

    @Unique
    private static boolean silky$applyEntityOverride(ResourceManager resourceManager, Map<Object, String> shaderSources) {
        String entityFragmentSrc = silky$loadShaderSource(resourceManager, SILKY_ENTITY_FRAGMENT);
        String entityVertexSrc = silky$loadShaderSource(resourceManager, SILKY_ENTITY_VERTEX);
        if (((entityFragmentSrc == null || entityFragmentSrc.isEmpty()) || (entityVertexSrc == null || entityVertexSrc.isEmpty())) && !silky$loggedEntityOverride) {
            silky$loggedEntityOverride = true;
            DebugLog.warn("[ShaderOverride] entity fade override skipped: silky entity_dither shader missing");
        }

        if ((entityFragmentSrc == null || entityFragmentSrc.isEmpty()) || (entityVertexSrc == null || entityVertexSrc.isEmpty())) {
            return false;
        }

        boolean entityFragmentChanged = false;
        boolean entityVertexChanged = false;

        for (Map.Entry<Object, String> entry : shaderSources.entrySet()) {
            Object key = entry.getKey();
            if (entityVertexSrc != null && !entityVertexSrc.isEmpty() && silky$isEntityVertexKey(key)) {
                entry.setValue(entityVertexSrc);
                entityVertexChanged = true;
                continue;
            }
            if (entityFragmentSrc != null && !entityFragmentSrc.isEmpty() && silky$isEntityFragmentKey(key)) {
                entry.setValue(entityFragmentSrc);
                entityFragmentChanged = true;
            }
        }

        if ((!entityFragmentChanged || !entityVertexChanged) && !silky$loggedEntityOverride) {
            silky$loggedEntityOverride = true;
            DebugLog.warn("[ShaderOverride] entity fade override skipped: entity shader key not found");
        }

        if (entityFragmentChanged && entityVertexChanged && !silky$loggedEntityOverride) {
            silky$loggedEntityOverride = true;
            DebugLog.renderThread("[ShaderOverride] override applied: core/entity -> silky entity_dither");
        }

        return entityFragmentChanged || entityVertexChanged;
    }

    @Unique
    private static boolean silky$isEntityFragmentKey(Object key) {
        if (!(key instanceof ShaderSourceKeyAccessor accessor)) return false;
        return VANILLA_ENTITY_ID.equals(accessor.silky$getId()) && accessor.silky$getType() == ShaderType.FRAGMENT;
    }

    @Unique
    private static boolean silky$isEntityVertexKey(Object key) {
        if (!(key instanceof ShaderSourceKeyAccessor accessor)) return false;
        return VANILLA_ENTITY_ID.equals(accessor.silky$getId()) && accessor.silky$getType() == ShaderType.VERTEX;
    }

    @Unique
    private static String silky$loadShaderSource(ResourceManager resourceManager, Identifier id) {
        try {
            return SilkyShaderSources.load(resourceManager, id);
        } catch (Exception e) {
            DebugLog.error("[ShaderOverride] read shader override failed: " + id, e);
            return null;
        }
    }

    @Unique
    private static Object silky$newShaderSourceKey(Identifier id, ShaderType type) {
        try {
            if (silky$shaderSourceKeyConstructor == null) {
                Class<?> keyClass = Class.forName("net.minecraft.client.renderer.ShaderManager$ShaderSourceKey");
                Constructor<?> constructor = keyClass.getDeclaredConstructor(Identifier.class, ShaderType.class);
                constructor.setAccessible(true);
                silky$shaderSourceKeyConstructor = constructor;
            }
            return silky$shaderSourceKeyConstructor.newInstance(id, type);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to create ShaderManager shader source key for " + type + " " + id, e);
        }
    }

    @Inject(method = "apply", at = @At("HEAD"))
    private void silky$beforeShaderReload(ShaderManager.Configs definitions,
                                              ResourceManager resourceManager,
                                              ProfilerFiller profiler,
                                              CallbackInfo ci) {
        RenderResourceReadiness.markReloading("shader reload");
    }

    @Inject(method = "apply", at = @At("TAIL"))
    private void silky$afterShaderReload(ShaderManager.Configs definitions,
                                             ResourceManager resourceManager,
                                             ProfilerFiller profiler,
                                             CallbackInfo ci) {
        ResourceReloadHooks.onReload(resourceManager);
    }

    @ModifyReturnValue(method = "prepare", at = @At("RETURN"))
    private ShaderManager.Configs silky$extendShaderSources(ShaderManager.Configs definitions,
                                                                ResourceManager resourceManager,
                                                                ProfilerFiller profiler) {
        ShaderLoaderDefinitionsAccessor accessor = (ShaderLoaderDefinitionsAccessor) (Object) definitions;
        Map<?, String> shaderSources = accessor.silky$getShaderSources();
        Map<Object, String> replaced = new LinkedHashMap<>(shaderSources.size() + 64);
        replaced.putAll(shaderSources);

        boolean changed = silky$addExtendedShaderSources(resourceManager, replaced);
        if (silky$allowVanillaEntityShaderOverride()) {
            changed |= silky$applyEntityOverride(resourceManager, replaced);
        }

        if (!changed) {
            return definitions;
        }

        Map<Identifier, PostChainConfig> postChains = accessor.silky$getPostChains();
        @SuppressWarnings({"rawtypes", "unchecked"})
        ShaderManager.Configs updated = new ShaderManager.Configs((Map) replaced, postChains);
        return updated;
    }
}
