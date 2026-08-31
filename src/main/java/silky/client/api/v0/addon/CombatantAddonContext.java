/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.api.v0.addon;

import silky.client.events.UsedImplicitly;
import silky.client.features.command.ClientCommand;
import silky.client.api.v0.clickgui.SilkyClickGuiSection;
import silky.client.features.gui.hud.AbstractHudElement;
import silky.client.features.gui.hud.draggable.DraggableHudElement;
import silky.client.features.module.Module;
import silky.client.api.v0.module.SilkyModuleExtension;
import silky.client.api.v0.render.SilkyPostProcessCallback;
import silky.client.api.v0.render.SilkyRenderCallback;
import silky.client.api.v0.render.SilkyRenderStage;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import silky.client.render.engine.postprocess.PostProcessPass;
import net.fabricmc.loader.api.ModContainer;

import java.nio.file.Path;

public interface SilkyAddonContext extends SilkyAddonRuntimeContext {
    @UsedImplicitly
    ModContainer modContainer();
    @UsedImplicitly
    Path gameDir();

    Path configDir();
    @UsedImplicitly
    Path addonConfigDir();
    @UsedImplicitly
    void registerModule(Module module);
    @UsedImplicitly
    void registerDraggableHudElement(DraggableHudElement element);
    @UsedImplicitly
    void registerStaticHudElement(AbstractHudElement element);
    @UsedImplicitly
    void registerCommand(ClientCommand command);
    @UsedImplicitly
    void registerClickGuiSection(String sectionId, String label, SilkyClickGuiSection section);
    @UsedImplicitly
    void registerModuleExtension(String moduleId, SilkyModuleExtension extension);
    @UsedImplicitly
    void registerIrisShaderPatchManifest(String manifestResourcePath);
    @UsedImplicitly
    void registerRenderCallback(String callbackId, SilkyRenderStage stage, SilkyRenderCallback callback);
    @UsedImplicitly
    void registerPostProcessPass(String passId, PostProcessPass.Phase phase, int priority, SilkyPostProcessCallback callback);
    @UsedImplicitly
    RenderPipeline registerRenderPipeline(RenderPipeline pipeline);
}
