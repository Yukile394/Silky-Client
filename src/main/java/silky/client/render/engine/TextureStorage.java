/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.render.engine;

import silky.client.util.resources.asset.AssetAutoLoader;
import silky.client.util.resources.asset.AssetLoad;
import silky.client.util.resources.asset.TextureCatalog;
import silky.client.util.logging.DebugLog;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.Identifier;

@TextureCatalog
public enum TextureStorage {
    ;
    public static final Identifier BLOOM =
            Identifier.fromNamespaceAndPath("silky", "textures/bloom.png");
    public static final Identifier FIRE_FLY =
            Identifier.fromNamespaceAndPath("silky", "textures/particles/firefly.png");
    public static final Identifier DEFAULT_CIRCLE =
            Identifier.fromNamespaceAndPath("silky", "textures/particles/default_circle.png");
    public static final Identifier JUMP_CIRCLE =
            Identifier.fromNamespaceAndPath("silky", "textures/particles/jump_circle.png");
    public static final Identifier BUBBLE =
            Identifier.fromNamespaceAndPath("silky", "textures/particles/bubble.png");
    public static final Identifier FUNNEL_EYE =
            Identifier.fromNamespaceAndPath("silky", "textures/particles/funnel/funnel_eye.png");
    public static final Identifier FUNNEL_DISTORTION =
            Identifier.fromNamespaceAndPath("silky", "textures/particles/funnel/distortion.png");
    public static final Identifier PARTICLE_STARS =
            Identifier.fromNamespaceAndPath("silky", "textures/particles/star.png");
    public static final Identifier[] RANDOM_PARTICLES = new Identifier[]{
            Identifier.fromNamespaceAndPath("silky", "textures/particles/p1.png"),
            Identifier.fromNamespaceAndPath("silky", "textures/particles/p2.png"),
            Identifier.fromNamespaceAndPath("silky", "textures/particles/p3.png"),
            Identifier.fromNamespaceAndPath("silky", "textures/particles/p4.png"),
            Identifier.fromNamespaceAndPath("silky", "textures/particles/p5.png"),
            Identifier.fromNamespaceAndPath("silky", "textures/particles/p7.png"),
            Identifier.fromNamespaceAndPath("silky", "textures/particles/p8.png"),
            Identifier.fromNamespaceAndPath("silky", "textures/particles/p9.png"),
            Identifier.fromNamespaceAndPath("silky", "textures/particles/p10.png"),
            Identifier.fromNamespaceAndPath("silky", "textures/particles/p11.png"),
            Identifier.fromNamespaceAndPath("silky", "textures/particles/p12.png")
    };
    public static final Identifier CAPTURE =
            Identifier.fromNamespaceAndPath("silky", "textures/hud/elements/capture.png");

    @AssetLoad(order = 200)
    public static void preload() {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) return;
        TextureManager tm = mc.getTextureManager();
        int loaded = AssetAutoLoader.preloadTextures(tm);
        DebugLog.renderThread("[Silky][Assets] preloaded %d textures", loaded);
    }
}
