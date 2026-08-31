/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.ClickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import silky.client.features.command.CommandManager;
import silky.client.features.gui.hud.nondraggable.impl.BetterButtons;
import silky.client.features.module.Modules;
import silky.client.features.module.modules.visuals.NoRender;
import silky.client.render.engine.core.SilkyRenderSystem;
import silky.client.render.engine.core.RenderPhase;
import silky.client.render.engine.core.RenderPhaseScope;
import silky.client.util.NarratorBlocker;

@Mixin(Screen.class)
public abstract class ScreenMixin {
    @Unique
    private RenderPhaseScope silky$screenPhaseScope;

    @Inject(method = "defaultHandleGameClickEvent", at = @At("HEAD"), cancellable = true)
    private static void silky$runClientChatCommand(ClickEvent event,
                                                       Minecraft client,
                                                       Screen screen,
                                                       CallbackInfo ci) {
        if (!(event instanceof ClickEvent.RunCommand run)) return;
        String command = run.command();
        if (command == null || !command.startsWith("@")) return;
        if (CommandManager.handle(command)) {
            ci.cancel();
        }
    }

    @Inject(method = "shouldRunNarration", at = @At("HEAD"), cancellable = true)
    private void silky$disableNarratorUi(CallbackInfoReturnable<Boolean> cir) {
        if (!NarratorBlocker.isBlocked()) return;
        cir.setReturnValue(false);
    }

    @Inject(method = "extractBackground(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V", at = @At("HEAD"), cancellable = true)
    private void silky$noRender$cancelDarkening(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        NoRender module = Modules.get(NoRender.class);
        Minecraft mc = Minecraft.getInstance();
        if (module != null && mc != null && mc.level != null && module.screenDarkeningDisabled()) {
            ci.cancel();
        }
    }

    @Inject(method = "extractTransparentBackground(Lnet/minecraft/client/gui/GuiGraphicsExtractor;)V", at = @At("HEAD"), cancellable = true)
    private void silky$noRender$cancelInGameBackground(GuiGraphicsExtractor context, CallbackInfo ci) {
        NoRender module = Modules.get(NoRender.class);
        Minecraft mc = Minecraft.getInstance();
        if (module != null && mc != null && mc.level != null && module.screenDarkeningDisabled()) {
            ci.cancel();
        }
    }

    @Inject(method = "extractRenderStateWithTooltipAndSubtitles(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V", at = @At("HEAD"))
    private void silky$beginButtonQueue(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        BetterButtons.beginFrame(ctx);
    }

    @Inject(method = "extractRenderStateWithTooltipAndSubtitles(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V", at = @At("HEAD"))
    private void silky$phaseScreenHead(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        Screen self = (Screen) (Object) this;
        String label = "2d:screen:" + self.getClass().getSimpleName();
        SilkyRenderSystem.ensureFrameContext();
        silky$screenPhaseScope = SilkyRenderSystem.phase(RenderPhase.SCREEN, label);
    }

    @Inject(method = "extractRenderStateWithTooltipAndSubtitles(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V", at = @At("RETURN"))
    private void silky$phaseScreenTail(GuiGraphicsExtractor ctx, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        Screen self = (Screen) (Object) this;
        String label = "2d:screen:" + self.getClass().getSimpleName();
        if (silky$screenPhaseScope != null) {
            silky$screenPhaseScope.close();
            silky$screenPhaseScope = null;
        }
    }
}

