/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.mixins;

import net.minecraft.client.gui.components.AbstractWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import silky.client.mixininterface.IRoundedHitbox;

@Mixin(AbstractWidget.class)
public abstract class AbstractWidgetMixin implements IRoundedHitbox {

    @Unique
    private float silky$roundedRadius = 0.0f;
    @Unique
    private boolean silky$roundedEnabled = false;

    @Override
    public void silky$setRoundedHitbox(float radius) {
        silky$roundedRadius = Math.max(0.0f, radius);
        silky$roundedEnabled = silky$roundedRadius > 0.0f;
    }

    @Override
    public void silky$clearRoundedHitbox() {
        silky$roundedRadius = 0.0f;
        silky$roundedEnabled = false;
    }

    @Override
    public float silky$getRoundedHitbox() {
        return silky$roundedRadius;
    }

    @Override
    public boolean silky$useRoundedHitbox() {
        return silky$roundedEnabled;
    }

    @Inject(method = "isMouseOver(DD)Z", at = @At("HEAD"), cancellable = true)
    private void silky$isMouseOverRounded(double mouseX, double mouseY, CallbackInfoReturnable<Boolean> cir) {
        if (!silky$roundedEnabled) return;

        AbstractWidget self = (AbstractWidget) (Object) this;
        float x = self.getX();
        float y = self.getY();
        float w = self.getWidth();
        float h = self.getHeight();

        if (mouseX < x || mouseY < y || mouseX > x + w || mouseY > y + h) {
            cir.setReturnValue(false);
            return;
        }

        float r = Math.min(silky$roundedRadius, Math.min(w, h) * 0.5f);
        if (r <= 0.0f) {
            cir.setReturnValue(true);
            return;
        }

        float px = (float) (mouseX - x);
        float py = (float) (mouseY - y);

        if ((px >= r && px <= w - r) || (py >= r && py <= h - r)) {
            cir.setReturnValue(true);
            return;
        }

        float cx = (px < r) ? r : (w - r);
        float cy = (py < r) ? r : (h - r);
        float dx = px - cx;
        float dy = py - cy;
        cir.setReturnValue((dx * dx + dy * dy) <= (r * r));
    }
}




