/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.render.engine.renderer.ui.runtime.script;

public enum UiScriptEngineProvider {
    ;

    public static UiScriptEngine javet() {
        return new JavetUiScriptEngine();
    }
}
