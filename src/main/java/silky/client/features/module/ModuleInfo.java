/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.features.module;

import silky.client.events.UsedImplicitly;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@UsedImplicitly
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ModuleInfo {
    String id();

    String displayName();

    String[] aliases() default {};

    ModuleCategory category();

    /**
     * Plain description or i18n key. Optional, but carried by Module metadata when present.
     */
    String description() default "";

    boolean enabledByDefault() default false;
}
