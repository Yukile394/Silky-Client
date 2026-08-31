/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.render.engine.guard;

import silky.client.render.engine.core.SilkyRenderSystem;

/**
 * Final architecture lock for the Silky RHI migration.
 * <p>
 * This class is intentionally small and dependency-light: it is used from low-level legacy facades to make sure old
 * render paths cannot silently become production paths again. A path can only be enabled through an explicit system
 * property, and every use is counted in RHI stats.
 */
public enum RenderArchitectureGuard {
    ;
    public static final boolean DEBUG = Boolean.getBoolean("silky.render.debug");
    public static final boolean STRICT = !Boolean.getBoolean("silky.render.allowLegacyPaths");
    public static final boolean ALLOW_LEGACY_MESH_RENDERER = Boolean.getBoolean("silky.render.allowLegacyMeshRenderer");
    public static final boolean ALLOW_LEGACY_FULLSCREEN_MESH = Boolean.getBoolean("silky.render.allowLegacyFullscreenMesh");
    public static final boolean ALLOW_IMMEDIATE_UPLOAD = Boolean.getBoolean("silky.rhi.allowImmediateFallback")
            || Boolean.getBoolean("silky.render.allowImmediateUpload");

    public static void requireAllowed(LegacyRenderPath path, String owner) {
        boolean allowed = switch (path) {
            case IMMEDIATE_MESH_UPLOAD -> ALLOW_IMMEDIATE_UPLOAD;
            case LEGACY_FULLSCREEN_MESH -> ALLOW_LEGACY_FULLSCREEN_MESH;
            case LEGACY_MESH_RENDERER -> ALLOW_LEGACY_MESH_RENDERER;
            default -> !STRICT;
        };

        record(path);
        if (allowed) return;

        throw new IllegalStateException("Legacy render path is locked: " + path.description()
                + " owner=" + owner
                + ". Use SilkyRHI/SodiumGlBackend path instead. "
                + "Temporary override: -D" + overrideProperty(path) + "=true");
    }

    public static void record(LegacyRenderPath path) {
        try {
            SilkyRenderSystem.rhi().stats().legacyPath(path);
        } catch (Throwable ignored) {
            // Guard must be safe during bootstrap/static initialization.
        }
    }

    private static String overrideProperty(LegacyRenderPath path) {
        return switch (path) {
            case IMMEDIATE_MESH_UPLOAD -> "silky.rhi.allowImmediateFallback";
            case LEGACY_FULLSCREEN_MESH -> "silky.render.allowLegacyFullscreenMesh";
            case LEGACY_MESH_RENDERER -> "silky.render.allowLegacyMeshRenderer";
            default -> "silky.render.allowLegacyPaths";
        };
    }
}
