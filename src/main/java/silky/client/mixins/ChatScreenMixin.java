/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.mixins;

import net.minecraft.client.gui.ActiveTextCollector;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.gui.components.ChatComponent.DisplayMode;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import silky.client.features.command.CommandManager;
import silky.client.features.gui.chat.BetterChatRenderer;
import silky.client.features.gui.hud.draggable.impl.BetterChat;
import silky.client.mixins.accessors.ChatScreenSuggestorAccessor;
import silky.client.runtime.RuntimeGate;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {

    @Inject(method = "extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIF)V", at = @At("HEAD"), cancellable = true)
    private void silky$betterChat$render(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!RuntimeGate.canRunHud()) return;
        if (BetterChat.isActive()) {
            BetterChat cfg = BetterChat.get();
            if (cfg != null && cfg.hideVanilla()) {
                // Stash suggestion context; BetterChatRenderer will draw it in correct Z-order.
                BetterChatRenderer.setSuggestionContext((ChatScreen) (Object) this, context, mouseX, mouseY);
                ci.cancel();
            }
        }
    }

    @Redirect(
            method = "mouseClicked(Lnet/minecraft/client/input/MouseButtonEvent;Z)Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/components/ChatComponent;captureClickableText(Lnet/minecraft/client/gui/ActiveTextCollector;IILnet/minecraft/client/gui/components/ChatComponent$DisplayMode;)V"
            )
    )
    private void silky$betterChat$disableVanillaChatClickHitbox(ChatComponent chatHud,
                                                                    ActiveTextCollector textConsumer,
                                                                    int windowHeight,
                                                                    int currentTick,
                                                                    DisplayMode displayMode) {
        if (silky$betterChat$shouldHideVanillaChat()) {
            return;
        }
        chatHud.captureClickableText(textConsumer, windowHeight, currentTick, displayMode);
    }

    @Inject(method = "mouseScrolled(DDDD)Z", at = @At("HEAD"), cancellable = true)
    private void silky$betterChat$disableVanillaChatScroll(double mouseX,
                                                               double mouseY,
                                                               double horizontalAmount,
                                                               double verticalAmount,
                                                               CallbackInfoReturnable<Boolean> cir) {
        if (silky$betterChat$shouldHideVanillaChat()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "keyPressed(Lnet/minecraft/client/input/KeyEvent;)Z", at = @At("HEAD"))
    private void silky$betterChat$allowEscape(KeyEvent input, CallbackInfoReturnable<Boolean> cir) {
        if (!RuntimeGate.canRunHud()) return;
        if (!BetterChat.isActive()) return;
        if (!input.isEscape()) return;
        CommandSuggestions suggestor = ((ChatScreenSuggestorAccessor) this).getSilky$suggestor();
        if (suggestor != null) {
            suggestor.setAllowSuggestions(false);
            suggestor.hide();
        }
    }

    @Inject(method = "handleChatInput(Ljava/lang/String;Z)V", at = @At("HEAD"), cancellable = true, require = 0)
    private void silky$commands$sendMessage(String message, boolean addToHistory, CallbackInfo ci) {
        if (!RuntimeGate.canRunClientLogic()) return;
        if (CommandManager.handle(message)) {
            ci.cancel();
        }
    }

    @Unique
    private boolean silky$betterChat$shouldHideVanillaChat() {
        if (!RuntimeGate.canRunHud()) return false;
        if (!BetterChat.isActive()) return false;
        BetterChat cfg = BetterChat.get();
        return cfg != null && cfg.hideVanilla();
    }
}
