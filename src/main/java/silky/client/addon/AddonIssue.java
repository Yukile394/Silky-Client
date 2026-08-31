/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.addon;

public record AddonIssue(
        String addonId,
        Severity severity,
        String message,
        String detail
) {
    public enum Severity {
        INFO,
        WARNING,
        ERROR
    }
}
