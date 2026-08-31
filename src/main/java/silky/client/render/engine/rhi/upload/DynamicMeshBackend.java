/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.render.engine.rhi.upload;

import silky.client.render.engine.rhi.GpuMeshHandle;
import silky.client.render.engine.uniform.MeshBuilder;

public interface DynamicMeshBackend extends AutoCloseable {
    GpuMeshHandle upload(MeshBuilder mesh);

    void beginFrame(long frameId);

    void endSubmission();

    void framePresented();

    @Override
    void close();
}
