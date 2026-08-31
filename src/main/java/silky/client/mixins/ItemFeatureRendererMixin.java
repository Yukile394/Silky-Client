/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import silky.client.features.hmi_recode.render.HmiModelQuadList;
import net.minecraft.client.renderer.feature.ItemFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ItemFeatureRenderer.class)
public abstract class ItemFeatureRendererMixin {
    @Unique
    private static final int SILKY_VIEW_FADE_DITHER_MARK = 0x00010001;
    @Unique
    private static final int SILKY_VIEW_FADE_A2C_MARK = 0x00010100;

    @Unique
    private static boolean silky$isViewFadeMarked(int argb) {
        int alpha = (argb >>> 24) & 0xFF;
        if (alpha >= 255) {
            return false;
        }
        int rgbMarker = argb & 0x00010101;
        return rgbMarker == SILKY_VIEW_FADE_DITHER_MARK || rgbMarker == SILKY_VIEW_FADE_A2C_MARK;
    }

    @Unique
    private static boolean silky$hasViewFadeTint(int[] tints) {
        return tints != null && tints.length > 0 && silky$isViewFadeMarked(tints[0]);
    }

    @ModifyReturnValue(
            method = "getLayerColorSafe([ILnet/minecraft/client/resources/model/geometry/BakedQuad$MaterialInfo;)I",
            at = @At("RETURN")
    )
    private static int silky$applyViewFadeToUntintedItemQuads(int original,
                                                                  int[] tints,
                                                                  BakedQuad.MaterialInfo materialInfo) {
        if (original != -1 || !silky$hasViewFadeTint(tints)) {
            return original;
        }
        return tints[0];
    }

    @ModifyExpressionValue(
            method = "prepareMainSubmit",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/geometry/BakedQuad$MaterialInfo;itemRenderType()Lnet/minecraft/client/renderer/rendertype/RenderType;")
    )
    private RenderType silky$useTranslucentRenderTypeForViewFadeItem(RenderType original,
                                                                         @Local(argsOnly = true) ItemFeatureRenderer.Submit submit,
                                                                         @Local(ordinal = 0) BakedQuad.MaterialInfo materialInfo) {
        // Vanilla item translucent sheets render into ITEM_ENTITY_TARGET. For entity fade this can
        // corrupt the following world translucent composite, so item layers keep their own type.
        return original;
    }
    @ModifyExpressionValue(
            method = "prepareMainSubmit",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/feature/ItemFeatureRenderer$Submit;pose()Lcom/mojang/blaze3d/vertex/PoseStack$Pose;")
    )
    private PoseStack.Pose silky$applyHmiModelPoseMain(PoseStack.Pose original,
                                                            @Local(argsOnly = true) ItemFeatureRenderer.Submit submit,
                                                            @Local BakedQuad quad) {
        return HmiModelQuadList.transform(submit.quads(), original, quad);
    }

    @ModifyExpressionValue(
            method = "prepareOutlineSubmit",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/feature/ItemFeatureRenderer$Submit;pose()Lcom/mojang/blaze3d/vertex/PoseStack$Pose;")
    )
    private PoseStack.Pose silky$applyHmiModelPoseOutline(PoseStack.Pose original,
                                                               @Local(argsOnly = true) ItemFeatureRenderer.Submit submit,
                                                               @Local BakedQuad quad) {
        return HmiModelQuadList.transform(submit.quads(), original, quad);
    }

    @ModifyExpressionValue(
            method = "prepareFoilSubmit",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/feature/ItemFeatureRenderer$Submit;pose()Lcom/mojang/blaze3d/vertex/PoseStack$Pose;",
                    ordinal = 1
            )
    )
    private PoseStack.Pose silky$applyHmiModelPoseFoil(PoseStack.Pose original,
                                                            @Local(argsOnly = true) ItemFeatureRenderer.Submit submit,
                                                            @Local BakedQuad quad) {
        return HmiModelQuadList.transform(submit.quads(), original, quad);
    }

    @ModifyArg(
            method = "prepareFoilSubmit",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/feature/ItemFeatureRenderer;getFoilBuffer(Lnet/minecraft/client/renderer/rendertype/RenderType;Lcom/mojang/blaze3d/vertex/PoseStack$Pose;)Lcom/mojang/blaze3d/vertex/VertexConsumer;"
            ),
            index = 1
    )
    private PoseStack.Pose silky$applyHmiModelPoseFoilDecal(PoseStack.Pose original,
                                                                 @Local(argsOnly = true) ItemFeatureRenderer.Submit submit,
                                                                 @Local BakedQuad quad) {
        return HmiModelQuadList.transform(submit.quads(), original, quad);
    }

}
