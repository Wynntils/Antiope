package com.wynntils.antiope.core;

import com.wynntils.antiope.core.type.CoreClosedException;
import com.wynntils.antiope.core.type.LogLevel;
import com.wynntils.antiope.util.FileUtils;
import com.wynntils.antiope.manager.activity.ActivityManager;
import com.wynntils.antiope.core.type.CreateParams;
import com.wynntils.antiope.core.type.GameSDKException;
import com.wynntils.antiope.manager.overlay.OverlayManager;
import com.wynntils.antiope.core.type.Result;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * The main component for accessing Discord's game SDK.
 * @see <a href="https://discordapp.com/developers/docs/game-sdk/discord#functions-in-the-sdk">
 *     https://discordapp.com/developers/docs/game-sdk/discord#functions-in-the-sdk</a>
 * @author JCM
 */
public class DiscordGameSDKCore implements AutoCloseable {
    private static final String LIBRARY_NAME = "discord_game_sdk_jni";
    private static boolean firstInitDone = false;

    /**
     * Extracts and initializes the native library.
     * This method also loads Discord's native library.
     * <p>
     * The JNI library is extracted from the classpath (e.g. the currently running JAR)
     * using {@link Class#getResourceAsStream(String)}.
     * Its path inside the JAR must be of the pattern {@code /native/{os}/{arch}/{object name}}
     * where {@code os} is either "windows" or "linux", {@code arch} is the system architecture as in
     * the system property {@code os.arch} and {@code object name} is the name of the native object
     * (e.g. "discord_game_sdk_jni.dll" on Windows or "libdiscord_game_sdk_jni.so" on Linux.
     * <p>
     * You may call this method more than once which unloads the old shared object and loads the new one.
     **
     * @throws UnsatisfiedLinkError if Discord's native library can not be loaded
     */
    public static void loadLibrary() {
        String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        String arch = System.getProperty("os.arch").toLowerCase(Locale.ROOT);

        String objectName;

        String preloadDiscordSdk;

        if (osName.contains("windows")) {
            osName = "windows";
            objectName = LIBRARY_NAME + ".dll";

            // we don't want to rename the game sdk's internals, so we need to rename our arch temporarily
            if (arch.equals("amd64")) arch = "x86_64";
            preloadDiscordSdk = "/discord_game_sdk/lib/" + arch + "/discord_game_sdk.dll";
        } else if (osName.contains("linux")) {
            osName = "linux";
            objectName = "lib" + LIBRARY_NAME + ".so";

            preloadDiscordSdk = null;
        } else if (osName.contains("mac os")) {
            osName = "macos";
            objectName = "lib" + LIBRARY_NAME + ".dylib";

            preloadDiscordSdk = null;
        } else {
            throw new RuntimeException("Cannot determine OS type: " + osName);
        }

        /*
        Some systems (e.g. Mac OS X) might report the architecture as "x86_64" instead of "amd64".
        While it would be possible to store the macOS dylib as "x86_x64" instead of "amd64",
        I personally prefer to keep the system architecture consistent.
        */
        if (arch.equals("x86_64")) arch = "amd64";

        if (preloadDiscordSdk != null) {
            File discordSdkFile = FileUtils.createTemporaryFileFromResource(preloadDiscordSdk);

            // Discord game sdk needs to be loaded first on windows
            System.load(discordSdkFile.getAbsolutePath());
        }

        String libraryPath = "/native/" + osName + "/" + arch + "/" + objectName;

        File libraryFile = FileUtils.createTemporaryFileFromResource(libraryPath);

        System.load(libraryFile.getAbsolutePath());
        initDiscordNative(libraryFile.getAbsolutePath());
        firstInitDone = true;
    }

    /**
     * Loads Discord's SDK library.
     * <p>
     * This does not extract nor load the JNI native library.
     * If you want to do that, please use {@link DiscordGameSDKCore#loadLibrary}
     * which extracts and loads the JNI native and then calls this method.
     * @param discordPath Location of Discord's native library.
     *                    <p>On Windows the filename (last component of the path) must be
     *                    "discord_game_sdk.dll" or an {@link UnsatisfiedLinkError} will occur.</p>
     *                    <p>On Linux the filename does not matter.</p>
     */
    private static native void initDiscordNative(String discordPath);

    /**
     * <p>Default callback to use for operation returning a {@link Result}.</p>
     *
     * <p>Checks if the result is {@link Result#OK} and throws a {@link GameSDKException} if it is not.</p>
     */
    public static final Consumer<Result> DEFAULT_CALLBACK = result -> {
        if (result != Result.OK) throw new GameSDKException(result);
    };

    /**
     * <p>Default log hook. Simply prints the log message
     * in pattern "<code>[level] message</code>" to {@link System#out}.</p>
     */
    public static final BiConsumer<LogLevel, String> DEFAULT_LOG_HOOK = (level, message) -> {
        System.out.printf("[%s] %s\n", level, message);
    };

    private final long pointer;

    private final CreateParams createParams;
    private final AtomicBoolean open = new AtomicBoolean(true);
    private final ReentrantLock lock = new ReentrantLock();

    private final ActivityManager activityManager;
    private final OverlayManager overlayManager;

    /**
     * Creates an instance of the SDK from {@link CreateParams} and
     * sets the log hook to {@link DiscordGameSDKCore#DEFAULT_LOG_HOOK}.
     *
     * Example:
     * <pre>{@code
     *  try(CreateParams params = new CreateParams())
     *  {
     *      params.setClientID(<client ID of your application>);
     *      params.setFlags(CreateParams.getDefaultFlags());
     *      try(Core core = new Core(params))
     *      {
     *          // do something with your Core
     *      }
     *  }}</pre>
     *
     * @param params Parameters to create Core from.
     * @see <a href="https://discordapp.com/developers/docs/game-sdk/discord#create">
     *     https://discordapp.com/developers/docs/game-sdk/discord#create</a>
     */
    public DiscordGameSDKCore(CreateParams params) {
        if (!firstInitDone) {
            throw new IllegalStateException("Tried to create Core before calling Core.loadLibrary()");
        }

        this.createParams = params;
        Object ret = create(params.getPointer());
        if (ret instanceof Result) {
            throw new GameSDKException((Result) ret);
        } else {
            pointer = (long) ret;
        }

        setLogHook(LogLevel.DEBUG, DEFAULT_LOG_HOOK);

        this.activityManager = new ActivityManager(getActivityManager(pointer), this);
        this.overlayManager = new OverlayManager(getOverlayManager(pointer), this);
    }

    private native Object create(long paramPointer);

    private native void destroy(long pointer);

    private native long getActivityManager(long pointer);

    private native long getOverlayManager(long pointer);

    private native void runCallbacks(long pointer);

    private native void setLogHook(long pointer, int minLevel, BiConsumer<LogLevel, String> logHook);

    /**
     * <p>Returns the {@link ActivityManager} associated with this core.</p>
     * <p>An ActivityManager is used to set the User's activity/status.</p>
     * @return An {@link ActivityManager}
     * @see <a href="https://discordapp.com/developers/docs/game-sdk/discord#getactivitymanager">
     *     https://discordapp.com/developers/docs/game-sdk/discord#getactivitymanager</a>
     */
    public ActivityManager activityManager() {
        return activityManager;
    }

    /**
     * <p>Returns the {@link OverlayManager} associated with this core.</p>
     * <p>An OverlayManager is used to control the overlay for this game.</p>
     * @return An {@link OverlayManager}
     * @see <a href="https://discordapp.com/developers/docs/game-sdk/discord#getoverlaymanager">
     *     https://discordapp.com/developers/docs/game-sdk/discord#getoverlaymanager</a>
     */
    public OverlayManager overlayManager() {
        return overlayManager;
    }

    /**
     * <p>Listens for new events and runs pending callbacks.</p>
     * <p>This method should be called in a main loop every few millis.</p>
     * @see <a href="https://discordapp.com/developers/docs/game-sdk/discord#setloghook">
     *     https://discordapp.com/developers/docs/game-sdk/discord#runcallbacks</a>
     */
    public void runCallbacks() {
        execute(() -> runCallbacks(pointer));
    }

    /**
     * Registers a log function.
     * @param minLevel Minimal level of message to receive.
     * @param logHook Hook to send log messages to.
     * @see DiscordGameSDKCore#DEFAULT_LOG_HOOK
     * @see <a href="https://discordapp.com/developers/docs/game-sdk/discord#setloghook">
     *     https://discordapp.com/developers/docs/game-sdk/discord#setloghook</a>
     */
    public void setLogHook(LogLevel minLevel, BiConsumer<LogLevel, String> logHook) {
        execute(() -> setLogHook(pointer, minLevel.ordinal(), Objects.requireNonNull(logHook)));
    }

    /**
     * Returns true if this {@link DiscordGameSDKCore} instance is open, i.e. {@link #close()} has not
     * been called yet. Calling certain SDK methods will throw {@link CoreClosedException}
     * if the {@link DiscordGameSDKCore} is not open.
     * @return True if this {@link DiscordGameSDKCore} is still open, false otherwise
     */
    public boolean isOpen() {
        return open.get();
    }

    /**
     * <p>Closes and destroys the instance.</p>
     * <p>This should be called at the end of the program.</p>
     *
     * @see <a href="https://discordapp.com/developers/docs/game-sdk/discord#destroy">
     *     https://discordapp.com/developers/docs/game-sdk/discord#destroy</a>
     */
    @Override
    public void close() {
        if (open.compareAndSet(true, false)) {
            lock.lock();
            try {
                destroy(pointer);
            } finally {
                lock.unlock();
            }
            createParams.close();
        }
    }

    /**
     * <p>Return the pointer to the native structure.</p>
     * <p>This is <b>not</b> an API method. Do <b>not</b> call it.</p>
     * @return A native pointer.
     */
    public long getPointer() {
        return pointer;
    }

    public <T> T execute(Supplier<T> provider) {
        if (!isOpen()) throw new CoreClosedException();

        lock.lock();
        try {
            return provider.get();
        } finally {
            lock.unlock();
        }
    }

    public void execute(Runnable runnable) {
        execute((Supplier<Void>) () -> {
            runnable.run();
            return null;
        });
    }
}
