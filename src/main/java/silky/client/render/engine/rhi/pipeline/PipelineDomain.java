/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.render.engine.rhi.pipeline;

/**
 * High-level render domain used by pass compilers and profilers.
 */
public enum PipelineDomain {
    WORLD,
    UI,
    FULLSCREEN,
    TEXT,
    POSTPROCESS,
    UNKNOWN
}
