/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.util.session.microsoft;

public final class DeviceAuthPendingException extends RuntimeException {
    public DeviceAuthPendingException(String message) {
        super(message);
    }

    public DeviceAuthPendingException(String message, Throwable cause) {
        super(message, cause);
    }
}
