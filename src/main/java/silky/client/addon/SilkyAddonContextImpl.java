/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.addon;

import silky.client.api.v0.addon.SilkyAddonContext;
import silky.client.api.v0.clickgui.SilkyClickGuiSection;
import silky.client.api.v0.client.SilkyClientApi;
import silky.client.api.v0.module.SilkyModuleExtension;
import silky.client.api.v0.render.SilkyPostProcessCallback;
import silky.client.api.v0.render.SilkyRenderCallback;
import silky.client.api.v0.render.SilkyRenderStage;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import silky.client.features.command.ClientCommand;
import silky.client.features.command.CommandManager;
import silky.client.features.gui.hud.AbstractHudElement;
import silky.client.features.gui.hud.draggable.DraggableHudElement;
import silky.client.features.gui.hud.draggable.DraggableHudElementRegistry;
import silky.client.features.gui.hud.nondraggable.StaticHudElementRegistry;
import silky.client.features.module.Module;
import silky.client.render.engine.pipeline.SilkyRenderPipelines;
import silky.client.render.engine.postprocess.PostProcessPass;
import silky.client.render.iris.patch.ShaderPatchEngine;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.nio.file.Path;

final class SilkyAddonContextImpl implements SilkyAddonContext {
    private final AddonRegistration registration;

    SilkyAddonContextImpl(AddonRegistration registration) {
        this.registration = registration;
    }

    @Override
    public String addonId() {
        return registration.descriptor.id();
    }

    @Override
    public SilkyClientApi client() {
        return SilkyClientApi.get();
    }

    @Override
    public ModContainer modContainer() {
        return registration.entrypoint.getProvider();
    }

    @Override
    public Path gameDir() {
        return FabricLoader.getInstance().getGameDir();
    }

    @Override
    public Path configDir() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public Path addonConfigDir() {
        return configDir().resolve("silky").resolve("addons").resolve(addonId());
    }

    @Override
    public void registerModule(Module module) {
        if (module == null) return;
        module.postInit();
        registration.modules.add(module.name());
        AddonManager.noteModuleOwner(addonId(), module.name());
    }

    @Override
    public void registerDraggableHudElement(DraggableHudElement element) {
        if (element == null) return;
        DraggableHudElementRegistry.register(element);
        registration.draggableHudElements.add(element.getId());
        AddonManager.noteDraggableHudOwner(addonId(), element.getId());
    }

    @Override
    public void registerStaticHudElement(AbstractHudElement element) {
        if (element == null) return;
        StaticHudElementRegistry.register(element);
        registration.staticHudElements.add(element.getId());
        AddonManager.noteStaticHudOwner(addonId(), element.getId());
    }

    @Override
    public void registerCommand(ClientCommand command) {
        if (command == null) return;
        CommandManager.register(new AddonClientCommand(addonId(), command));
        registration.commands.add(command.metadata().id());
    }

    @Override
    public void registerClickGuiSection(String sectionId, String label, SilkyClickGuiSection section) {
        if (ClickGuiSectionManager.register(addonId(), sectionId, label, section)) {
            registration.clickGuiSections.add(sectionId);
        } else {
            registration.issue(AddonIssue.Severity.WARNING, "ClickGui section registration failed", sectionId);
        }
    }

    @Override
    public void registerModuleExtension(String moduleId, SilkyModuleExtension extension) {
        if (ModuleExtensionManager.register(addonId(), moduleId, extension)) {
            registration.moduleExtensions.add(moduleId);
        } else {
            registration.issue(AddonIssue.Severity.WARNING, "Module extension target is unavailable", moduleId);
        }
    }

    @Override
    public void registerIrisShaderPatchManifest(String manifestResourcePath) {
        if (ShaderPatchEngine.registerManifestResource(manifestResourcePath)) {
            registration.irisPatchManifests.add(manifestResourcePath);
        } else {
            registration.issue(AddonIssue.Severity.WARNING, "Iris patch manifest registration failed", manifestResourcePath);
        }
    }

    @Override
    public void registerRenderCallback(String callbackId, SilkyRenderStage stage, SilkyRenderCallback callback) {
        if (AddonRenderPipelineManager.register(addonId(), callbackId, stage, callback)) {
            registration.renderCallbacks.add(stage.name().toLowerCase(java.util.Locale.ROOT) + ":" + callbackId);
        } else {
            registration.issue(AddonIssue.Severity.WARNING, "Render callback registration failed", callbackId);
        }
    }

    @Override
    public void registerPostProcessPass(String passId,
                                        PostProcessPass.Phase phase,
                                        int priority,
                                        SilkyPostProcessCallback callback) {
        if (AddonRenderPipelineManager.registerPostProcess(addonId(), passId, phase, priority, callback)) {
            registration.postProcessPasses.add(phase.name().toLowerCase(java.util.Locale.ROOT) + ":" + passId);
        } else {
            registration.issue(AddonIssue.Severity.WARNING, "Post-process registration failed", passId);
        }
    }

    @Override
    public RenderPipeline registerRenderPipeline(RenderPipeline pipeline) {
        if (pipeline == null) return null;
        RenderPipeline registered = SilkyRenderPipelines.registerAddonPipeline(pipeline);
        registration.renderCallbacks.add("pipeline:" + pipeline.getLocation());
        return registered;
    }

    private record AddonClientCommand(String addonId, ClientCommand delegate) implements ClientCommand {
        @Override
        public silky.client.features.command.CommandMetadata metadata() {
            return delegate.metadata();
        }

        @Deprecated(forRemoval = true)
        @Override
        @SuppressWarnings("removal")
        public String name() {
            return delegate.name();
        }

        @Deprecated(forRemoval = true)
        @Override
        @SuppressWarnings("removal")
        public java.util.List<String> aliases() {
            return delegate.aliases();
        }

        @Deprecated(forRemoval = true)
        @Override
        @SuppressWarnings("removal")
        public String usage() {
            return delegate.usage();
        }

        @Deprecated(forRemoval = true)
        @Override
        @SuppressWarnings("removal")
        public String description() {
            return delegate.description();
        }

        @Override
        public boolean isAvailable() {
            return AddonManager.isActive(addonId) && delegate.isAvailable();
        }

        @Override
        public java.util.List<String> suggest(silky.client.features.command.CommandContext ctx, int argIndex, String token) {
            return delegate.suggest(ctx, argIndex, token);
        }

        @Override
        public boolean execute(silky.client.features.command.CommandContext ctx) {
            return isAvailable() && delegate.execute(ctx);
        }
    }
}
