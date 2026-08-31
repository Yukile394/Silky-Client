/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.render.iris.patch;

import silky.client.util.resources.asset.ResourceAsset;
import silky.client.util.resources.asset.ResourceCatalog;

/** Shader-patch resources resolved through the central asset metadata registry. */
@ResourceCatalog(namespace = "silky", root = "shaders/iris-patches")
public enum ShaderPatchResources {
    @ResourceAsset("index.json")
    INDEX
}
