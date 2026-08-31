/*
 * This file is part of the Silky Client distribution.
 * Copyright (c) 2026 pivosos2007.
 *
 * Licensed under the GNU General Public License v3.0.
 */

package silky.client.util.session.microsoft;

public class MicrosoftAuthException extends RuntimeException {
    private final String key;

    public MicrosoftAuthException(String message) {
        this(message, null, "silky.auth.microsoft.error");
    }

    public MicrosoftAuthException(String message, Throwable cause) {
        this(message, cause, "silky.auth.microsoft.error");
    }

    public MicrosoftAuthException(String message, String key) {
        this(message, null, key);
    }

    public MicrosoftAuthException(String message, Throwable cause, String key) {
        super(message + (key == null || key.isBlank() ? "" : " (key: " + key + ")"), cause);
        this.key = key == null ? "silky.auth.microsoft.error" : key;
    }

    public String key() {
        return key;
    }
}
