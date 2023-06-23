package com.wynntils.antiope.core.type;

import com.wynntils.antiope.core.DiscordGameSDKCore;

/**
 * Exception which is thrown when attempting to execute an SDK operation
 * when the {@link DiscordGameSDKCore} has been closed.
 */
public class CoreClosedException extends IllegalStateException {
    public CoreClosedException() {
        super("Core is closed");
    }
}
