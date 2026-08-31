/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import silky.client.features.module.modules.visuals.ViewModel;
import silky.client.util.screen.ClientScreen;
import silky.client.features.playeranimator.PlayerRigRenderContext;
import silky.client.features.playeranimator.PlayerRigRenderState;
import silky.client.features.playeranimator.render.PlayerRigCpuRenderer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import silky.client.features.module.Modules;
import silky.client.features.module.modules.visuals.NameTags;
import silky.client.features.module.modules.visuals.NoRender;
import silky.client.features.module.modules.visuals.SeeInvisibles;
import silky.client.render.ViewObstructionFadeContext;
import silky.client.render.ViewObstructionFadeState;
import silky.client.render.iris.IrisRuntime;
import silky.client.util.aiming.RotationManager;
import silky.client.util.aiming.data.Rotation;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, S extends LivingEntityRenderState> extends EntityRenderer<T, S> {
    protected LivingEntityRendererMixin(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Shadow
    public abstract Identifier getTextureLocation(S state);

    @Inject(
            method = "shouldShowName(Lnet/minecraft/world/entity/LivingEntity;D)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private void silky$hideVanillaLabels(T entity, double distanceSq, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof Player && Modules.get(NameTags.class) != null && Modules.get(NameTags.class).isEnabled()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
            method = "extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V",
            at = @At("TAIL")
    )
    private void silky$updateViewObstructionFadeState(T entity, S state, float tickProgress, CallbackInfo ci) {
        if (!(state instanceof ViewObstructionFadeState fadeState)) {
            return;
        }

        NoRender noRender = Modules.get(NoRender.class);
        boolean obstructionFadeActive = noRender != null && noRender.shouldFadeEntity(entity);
        float obstructionFadeAlpha = obstructionFadeActive ? noRender.getEntityFadeAlpha(entity) : 1.0f;
        obstructionFadeActive = obstructionFadeActive && obstructionFadeAlpha < 0.99f;

        SeeInvisibles seeInvisibles = Modules.get(SeeInvisibles.class);
        boolean seeInvisibleActive = seeInvisibles != null && seeInvisibles.shouldRenderInvisiblePlayer(entity);
        float seeInvisibleAlpha = seeInvisibleActive ? seeInvisibles.getInvisiblePlayerAlpha01() : 1.0f;
        boolean seeInvisibleFadeActive = seeInvisibleActive && seeInvisibleAlpha < 0.99f;
        if (seeInvisibleActive) {
            state.isInvisibleToPlayer = false;
        }

        boolean active = obstructionFadeActive || seeInvisibleFadeActive;
        float alpha = obstructionFadeAlpha;
        if (seeInvisibleFadeActive) {
            alpha = Math.min(alpha, seeInvisibleAlpha);
        }
        fadeState.silky$setViewObstructionFadeActive(active);
        fadeState.silky$setViewObstructionFadeAlpha(active ? alpha : 1.0f);
        fadeState.silky$setSeeInvisibleFadeActive(seeInvisibleFadeActive);
    }

    @WrapOperation(
            method = "submit",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/rendertype/RenderType;IIILnet/minecraft/client/renderer/texture/TextureAtlasSprite;ILnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V"
            )
    )
    private void silky$submitAnatomicalPlayer(
            SubmitNodeCollector collector,
            Model<?> model,
            Object modelState,
            PoseStack matrices,
            RenderType renderType,
            int light,
            int overlay,
            int tint,
            TextureAtlasSprite sprite,
            int outlineColor,
            ModelFeatureRenderer.CrumblingOverlay crumbling,
            Operation<Void> original
    ) {
        if (model instanceof PlayerModel playerModel
                && modelState instanceof AvatarRenderState avatarState
                && avatarState instanceof PlayerRigRenderState rigState
                && PlayerRigCpuRenderer.submitPlayer(
                        collector, playerModel, avatarState, rigState.silky$getPlayerRig(), matrices,
                        renderType, light, overlay, tint, sprite, outlineColor, crumbling
                )) {
            return;
        }
        original.call(collector, model, modelState, matrices, renderType, light, overlay, tint,
                sprite, outlineColor, crumbling);
    }

    @ModifyExpressionValue(
            method = "extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/Mth;rotLerp(FFF)F")
    )
    private float silky$renderRotationYaw(float original, @Local(argsOnly = true) T entity, @Local(argsOnly = true) float tickProgress) {
        if (!(entity instanceof LocalPlayer player)) {
            return original;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != player || ClientScreen.current() instanceof AbstractContainerScreen<?>) {
            return original;
        }

        Rotation current = RotationManager.INSTANCE.getCurrentRotation();
        Rotation previous = RotationManager.INSTANCE.getPreviousRotation();
        if (current == null) {
            return original;
        }

        float prevYaw = previous != null ? previous.yaw() : current.yaw();
        if (prevYaw == player.getYRot() && current.yaw() == player.getYRot()) {
            return original;
        }

        return Mth.rotLerp(tickProgress, prevYaw, current.yaw());
    }

    @ModifyExpressionValue(
            method = "extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getXRot(F)F")
    )
    private float silky$renderRotationPitch(float original, @Local(argsOnly = true) T entity, @Local(argsOnly = true) float tickProgress) {
        if (!(entity instanceof LocalPlayer player)) {
            return original;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != player || ClientScreen.current() instanceof AbstractContainerScreen<?>) {
            return original;
        }

        Rotation current = RotationManager.INSTANCE.getCurrentRotation();
        Rotation previous = RotationManager.INSTANCE.getPreviousRotation();
        if (current == null) {
            return original;
        }

        float prevPitch = previous != null ? previous.pitch() : current.pitch();
        if (prevPitch == player.getXRot() && current.pitch() == player.getXRot()) {
            return original;
        }

        return Mth.lerp(tickProgress, prevPitch, current.pitch());
    }


    @ModifyExpressionValue(
            method = "submit",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/LivingEntityRenderer;getRenderType(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;ZZZ)Lnet/minecraft/client/renderer/rendertype/RenderType;")
    )
    private RenderType silky$useTranslucentRenderTypeForViewFade(RenderType original, @Local(argsOnly = true) S state) {
        if (original == null || original.hasBlending()) {
            return original;
        }
        if (IrisRuntime.isRenderingShadowPass()) {
            return original;
        }
        if (!(state instanceof ViewObstructionFadeState fadeState)
                || !fadeState.silky$isViewObstructionFadeActive()
                || fadeState.silky$getViewObstructionFadeAlpha() >= 0.99f) {
            return original;
        }
        return RenderTypes.entityTranslucent(getTextureLocation(state), true);
    }

    @ModifyConstant(
            method = "submit",
            constant = @Constant(intValue = 654311423)
    )
    private int silky$seeInvisibles$overrideInvisiblePlayerAlpha(int vanillaTint, S state) {
        SeeInvisibles seeInvisibles = Modules.get(SeeInvisibles.class);
        if (seeInvisibles != null
                && state instanceof ViewObstructionFadeState fadeState
                && fadeState.silky$isSeeInvisibleFadeActive()
                && seeInvisibles.shouldRenderInvisiblePlayer(state.entityType, state.isInvisible)) {
            return seeInvisibles.getInvisiblePlayerTintArgb();
        }
        return vanillaTint;
    }

    @Inject(method = "submit", at = @At("HEAD"))
    private void silky$pushViewObstructionFadeContext(S state, com.mojang.blaze3d.vertex.PoseStack matrixStack, net.minecraft.client.renderer.SubmitNodeCollector orderedRenderCommandQueue, net.minecraft.client.renderer.state.level.CameraRenderState cameraRenderState, CallbackInfo ci) {
        ViewObstructionFadeContext.push(state instanceof ViewObstructionFadeState fadeState ? fadeState : null);
        PlayerRigRenderContext.push(state);
    }

    @Inject(method = "submit", at = @At("RETURN"))
    private void silky$popViewObstructionFadeContext(S state, com.mojang.blaze3d.vertex.PoseStack matrixStack, net.minecraft.client.renderer.SubmitNodeCollector orderedRenderCommandQueue, net.minecraft.client.renderer.state.level.CameraRenderState cameraRenderState, CallbackInfo ci) {
        PlayerRigRenderContext.pop();
        ViewObstructionFadeContext.pop();
    }
}
