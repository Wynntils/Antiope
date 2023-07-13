package com.wynntils.antiope.core.type;

/**
 * Exception which is thrown when a {@link Result} that is not {@link Result#OK} occurs.
 */
public class GameSDKException extends RuntimeException {
    private final Result result;

    public GameSDKException(Result result) {
        super("Game SDK operation failed: " + result);
        this.result = result;
    }

    /**
     * Non-{@link Result#OK} result that occurred.
     * @return Occurred {@link Result}
     */
    public Result getResult() {
        return result;
    }
}
