/*
 * This file is part of the Silky Client distribution.
 * Silky modifications copyright (c) 2026 pivosos2007.
 *
 * Portions of this file are based on, adapted from, or implemented
 * with reference to Meteor Client
 * (https://github.com/MeteorDevelopment/meteor-client).
 * Copyright (c) Meteor Development.
 *
 * Licensed under the GNU General Public License v3.0.
 * See THIRD_PARTY_NOTICES.md for details.
 */

package silky.client.render.engine.vertex;

import com.mojang.blaze3d.vertex.VertexFormat;

public enum SilkyVertexFormats {
    ;
    public static final VertexFormat POS2 = VertexFormat.builder(0)
            .addAttribute("Position", SilkyVertexFormatElements.POS2)
            .build();

    public static final VertexFormat POS3_TEXTURE_COLOR_PARAMS2 = VertexFormat.builder(0)
            .addAttribute("Position", SilkyVertexFormatElements.POS3)
            .addAttribute("UV0", SilkyVertexFormatElements.TEXTURE)
            .addAttribute("Color", SilkyVertexFormatElements.COLOR)
            .addAttribute("Params", SilkyVertexFormatElements.PARAMS)
            .addAttribute("Params2", SilkyVertexFormatElements.PARAMS2)
            .build();

    public static final VertexFormat POS2_COLOR = VertexFormat.builder(0)
            .addAttribute("Position", SilkyVertexFormatElements.POS2)
            .addAttribute("Color", SilkyVertexFormatElements.COLOR)
            .build();

    /**
     * World-space wide line vertex.
     * Position is the current endpoint, Color is endpoint color, Line packs the opposite endpoint in xyz
     * and signed screen-space line width in w. The vertex shader expands the endpoint in clip space.
     */
    public static final VertexFormat POS3_COLOR_LINE = VertexFormat.builder(0)
            .addAttribute("Position", SilkyVertexFormatElements.POS3)
            .addAttribute("Color", SilkyVertexFormatElements.COLOR)
            .addAttribute("Line", SilkyVertexFormatElements.PARAMS)
            .build();

    public static final VertexFormat POS2_TEXTURE_COLOR = VertexFormat.builder(0)
            .addAttribute("Position", SilkyVertexFormatElements.POS2)
            .addAttribute("UV0", SilkyVertexFormatElements.TEXTURE)
            .addAttribute("Color", SilkyVertexFormatElements.COLOR)
            .build();

    public static final VertexFormat POS2_COLOR_RECT_PARAMS = VertexFormat.builder(0)
            .addAttribute("Position", SilkyVertexFormatElements.POS2)
            .addAttribute("Color", SilkyVertexFormatElements.COLOR)
            .addAttribute("Rect", SilkyVertexFormatElements.RECT)
            .addAttribute("Params", SilkyVertexFormatElements.PARAMS)
            .build();

    public static final VertexFormat POS2_COLOR_RECT_PARAMS2 = VertexFormat.builder(0)
            .addAttribute("Position", SilkyVertexFormatElements.POS2)
            .addAttribute("Color", SilkyVertexFormatElements.COLOR)
            .addAttribute("Rect", SilkyVertexFormatElements.RECT)
            .addAttribute("Params", SilkyVertexFormatElements.PARAMS)
            .addAttribute("Params2", SilkyVertexFormatElements.PARAMS2)
            .build();

    public static final VertexFormat POS2_LOCAL_COLOR_RECT_PARAMS = VertexFormat.builder(0)
            .addAttribute("Position", SilkyVertexFormatElements.POS2)
            .addAttribute("Local", SilkyVertexFormatElements.LOCAL)
            .addAttribute("Color", SilkyVertexFormatElements.COLOR)
            .addAttribute("Rect", SilkyVertexFormatElements.RECT)
            .addAttribute("Params", SilkyVertexFormatElements.PARAMS)
            .build();

    public static final VertexFormat POS2_LOCAL_COLOR_RECT_PARAMS2 = VertexFormat.builder(0)
            .addAttribute("Position", SilkyVertexFormatElements.POS2)
            .addAttribute("Local", SilkyVertexFormatElements.LOCAL)
            .addAttribute("Color", SilkyVertexFormatElements.COLOR)
            .addAttribute("Rect", SilkyVertexFormatElements.RECT)
            .addAttribute("Params", SilkyVertexFormatElements.PARAMS)
            .addAttribute("Params2", SilkyVertexFormatElements.PARAMS2)
            .build();

    /** Shared data-driven UI geometry family: shape header plus two shape-specific payloads. */
    public static final VertexFormat POS2_LOCAL_COLOR_RECT_PARAMS3 = VertexFormat.builder(0)
            .addAttribute("Position", SilkyVertexFormatElements.POS2)
            .addAttribute("Local", SilkyVertexFormatElements.LOCAL)
            .addAttribute("Color", SilkyVertexFormatElements.COLOR)
            .addAttribute("Rect", SilkyVertexFormatElements.RECT)
            .addAttribute("Params", SilkyVertexFormatElements.PARAMS)
            .addAttribute("Params2", SilkyVertexFormatElements.PARAMS2)
            .addAttribute("Params3", SilkyVertexFormatElements.PARAMS3)
            .build();

    public static final VertexFormat POS2_TEXTURE_COLOR_RECT_PARAMS = VertexFormat.builder(0)
            .addAttribute("Position", SilkyVertexFormatElements.POS2)
            .addAttribute("UV0", SilkyVertexFormatElements.TEXTURE)
            .addAttribute("Color", SilkyVertexFormatElements.COLOR)
            .addAttribute("Rect", SilkyVertexFormatElements.RECT)
            .addAttribute("Params", SilkyVertexFormatElements.PARAMS)
            .build();

    public static final VertexFormat POS2_TEXTURE_LOCAL_COLOR_RECT_PARAMS = VertexFormat.builder(0)
            .addAttribute("Position", SilkyVertexFormatElements.POS2)
            .addAttribute("UV0", SilkyVertexFormatElements.TEXTURE)
            .addAttribute("Local", SilkyVertexFormatElements.LOCAL)
            .addAttribute("Color", SilkyVertexFormatElements.COLOR)
            .addAttribute("Rect", SilkyVertexFormatElements.RECT)
            .addAttribute("Params", SilkyVertexFormatElements.PARAMS)
            .build();

    public static final VertexFormat POS2_TEXTURE_LOCAL_COLOR_RECT_PARAMS2 = VertexFormat.builder(0)
            .addAttribute("Position", SilkyVertexFormatElements.POS2)
            .addAttribute("UV0", SilkyVertexFormatElements.TEXTURE)
            .addAttribute("Local", SilkyVertexFormatElements.LOCAL)
            .addAttribute("Color", SilkyVertexFormatElements.COLOR)
            .addAttribute("Rect", SilkyVertexFormatElements.RECT)
            .addAttribute("Params", SilkyVertexFormatElements.PARAMS)
            .addAttribute("Params2", SilkyVertexFormatElements.PARAMS2)
            .build();

    /** Liquid-glass optical payload plus an optional eight-point primitive mask. */
    public static final VertexFormat POS2_TEXTURE_LOCAL_COLOR_RECT_PARAMS6 = VertexFormat.builder(0)
            .addAttribute("Position", SilkyVertexFormatElements.POS2)
            .addAttribute("UV0", SilkyVertexFormatElements.TEXTURE)
            .addAttribute("Local", SilkyVertexFormatElements.LOCAL)
            .addAttribute("Color", SilkyVertexFormatElements.COLOR)
            .addAttribute("Rect", SilkyVertexFormatElements.RECT)
            .addAttribute("Params", SilkyVertexFormatElements.PARAMS)
            .addAttribute("Params2", SilkyVertexFormatElements.PARAMS2)
            .addAttribute("Params3", SilkyVertexFormatElements.PARAMS3)
            .addAttribute("Params4", SilkyVertexFormatElements.PARAMS4)
            .addAttribute("Params5", SilkyVertexFormatElements.PARAMS5)
            .addAttribute("Params6", SilkyVertexFormatElements.PARAMS6)
            .build();

    public static final VertexFormat POS2_TEXTURE_COLOR_RECT_PARAMS2 = VertexFormat.builder(0)
            .addAttribute("Position", SilkyVertexFormatElements.POS2)
            .addAttribute("UV0", SilkyVertexFormatElements.TEXTURE)
            .addAttribute("Color", SilkyVertexFormatElements.COLOR)
            .addAttribute("Rect", SilkyVertexFormatElements.RECT)
            .addAttribute("Params", SilkyVertexFormatElements.PARAMS)
            .addAttribute("Params2", SilkyVertexFormatElements.PARAMS2)
            .build();

    public static final VertexFormat POS2_LOCAL_COLOR_RECT_PARAMS5 = VertexFormat.builder(0)
            .addAttribute("Position", SilkyVertexFormatElements.POS2)
            .addAttribute("Local", SilkyVertexFormatElements.LOCAL)
            .addAttribute("Color", SilkyVertexFormatElements.COLOR)
            .addAttribute("Rect", SilkyVertexFormatElements.RECT)
            .addAttribute("Params", SilkyVertexFormatElements.PARAMS)
            .addAttribute("Params2", SilkyVertexFormatElements.PARAMS2)
            .addAttribute("Params3", SilkyVertexFormatElements.PARAMS3)
            .addAttribute("Params4", SilkyVertexFormatElements.PARAMS4)
            .addAttribute("Params5", SilkyVertexFormatElements.PARAMS5)
            .build();

    /**
     * Rigged textured 3D vertex. Attribute order is mirrored by rig_textured.vert.
     * 12 + 8 + 12 + 4 + 4 + 4 + 16 + 4 = 64 bytes.
     */
    public static final VertexFormat RIG_POSITION_TEXTURE_NORMAL_COLOR_BONES_DEFORM = VertexFormat.builder(0)
            .addAttribute("Position", SilkyVertexFormatElements.POS3)
            .addAttribute("UV0", SilkyVertexFormatElements.TEXTURE)
            .addAttribute("Normal", SilkyVertexFormatElements.NORMAL)
            .addAttribute("Color", SilkyVertexFormatElements.COLOR)
            .addAttribute("BoneIndices", SilkyVertexFormatElements.BONE_INDICES)
            .addAttribute("BoneWeights", SilkyVertexFormatElements.BONE_WEIGHTS)
            .addAttribute("DeformCoord", SilkyVertexFormatElements.DEFORM_COORD)
            .addAttribute("DeformMeta", SilkyVertexFormatElements.DEFORM_META)
            .build();

}
