package de.jcm.discordgamesdk;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * The main component for accessing Discord's game SDK.
 * @see <a href="https://discordapp.com/developers/docs/game-sdk/discord#functions-in-the-sdk">
 *     https://discordapp.com/developers/docs/game-sdk/discord#functions-in-the-sdk</a>
 * @author JCM
 */
public class Core implements AutoCloseable {
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
     *
     * @param discordLibrary Location of Discord's native library.
     *
     * @throws UnsatisfiedLinkError if Discord's native library can not be loaded
     */
    public static void init(File discordLibrary) {
        File tempDir = new File(System.getProperty("java.io.tmpdir"), "java-discord-game-sdk-" + System.nanoTime());
        if (!(tempDir.exists() && tempDir.isDirectory()) && !tempDir.mkdir())
            throw new RuntimeException(new IOException("Cannot create temporary directory"));
        tempDir.deleteOnExit();
        init(discordLibrary, tempDir);
    }

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
     *
     * @param discordLibrary Location of Discord's native library.
     * @param tempDir Temporary directory, to which the discordLibrary will be copied to avoid problems
     *                on Windows.
     *
     * @throws UnsatisfiedLinkError if Discord's native library can not be loaded
     */
    public static void init(File discordLibrary, File tempDir) {
        String name = "discord_game_sdk_jni";
        String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        String arch = System.getProperty("os.arch").toLowerCase(Locale.ROOT);

        String objectName;

        if (osName.contains("windows")) {
            osName = "windows";
            objectName = name + ".dll";

            // the Discord native library needs to be loaded before our JNI library on Windows
            System.load(discordLibrary.getAbsolutePath());
        } else if (osName.contains("linux")) {
            osName = "linux";
            objectName = "lib" + name + ".so";
        } else if (osName.contains("mac os")) {
            osName = "macos";
            objectName = "lib" + name + ".dylib";
        } else {
            throw new RuntimeException("cannot determine OS type: " + osName);
        }

        /*
        Some systems (e.g. Mac OS X) might report the architecture as "x86_64" instead of "amd64".
        While it would be possible to store the MacOS dylib as "x86_x64" instead of "amd64",
        I personally prefer to keep the system architecture consistent.
         */
        if (arch.equals("x86_64")) arch = "amd64";

        String path = "/native/" + osName + "/" + arch + "/" + objectName;
        InputStream in = Core.class.getResourceAsStream(path);
        if (in == null) throw new RuntimeException(new FileNotFoundException("cannot find native library at " + path));

        File temp = new File(tempDir, objectName);
        temp.deleteOnExit();

        try {
            Files.copy(in, temp.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.load(temp.getAbsolutePath());
        initDiscordNative(discordLibrary.getAbsolutePath());
    }

    /**
     * Extracts and initializes the native library.
     * This method also loads Discord's native library from any given URL.
     * <p>
     * The JNI library is extracted from the classpath (e.g. the currently running JAR)
     * using {@link Class#getResourceAsStream(String)}.
     * Its path inside the JAR must be of the pattern {@code /native/{os}/{arch}/{object name}}
     * where {@code os} is either "windows" or "linux", {@code arch} is the system architecture as in
     * the system property {@code os.arch} and {@code object name} is the name of the native object
     * (e.g. "discord_game_sdk_jni.dll" on Windows or "libdiscord_game_sdk_jni.so" on Linux.
     * <p>
     * You may call this method more than once which unloads the old shared object and loads the new one.
     * <p>
     * The URL will be read and copied into a temporary file.
     *
     * @param url URL from which to load Discord's native library.
     *
     * @throws UnsatisfiedLinkError if Discord's native library can not be loaded
     */
    public static void init(URL url) {
        String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        String protocol = url.getProtocol();
        if (protocol.equalsIgnoreCase("file")
                && (!osName.contains("windows") || url.getFile().endsWith("discord_game_sdk.dll"))) {
            try {
                File file = new File(url.toURI());
                init(file);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                InputStream in = url.openStream();

                String objectName;
                if (osName.contains("windows")) objectName = "discord_game_sdk.dll";
                else if (osName.contains("mac os")) objectName = "discord_game_sdk.dylib";
                else if (osName.contains("linux")) objectName = "discord_game_sdk.so";
                else throw new RuntimeException("cannot determine OS type: " + osName);

                File tempDir =
                        new File(System.getProperty("java.io.tmpdir"), "java-discord-game-sdk-" + System.nanoTime());
                if (!(tempDir.exists() && tempDir.isDirectory()) && !tempDir.mkdir())
                    throw new RuntimeException(new IOException("Cannot create temporary directory"));
                File temp = new File(tempDir, objectName);
                temp.deleteOnExit();

                Files.copy(in, temp.toPath());

                in.close();

                init(temp, tempDir);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Extracts and initializes the native library.
     * This method also loads Discord's native library from the classpath, which <b>must</b> be located
     * at {@code /lib/{arch}/{name}} where {@code arch} is the system architecture from {@code os.arch},
     * where {@code amd64} is replaced with {@code x86_64} for unification, and {@code name} is either
     * {@code discord_game_sdk.dll} for Windows, {@code discord_game_sdk.so} for Linux or {@code discord_game_sdk.dylib}
     * for macOS.
     * <p>
     * The JNI library is extracted from the classpath (e.g. the currently running JAR)
     * using {@link Class#getResourceAsStream(String)}.
     * Its path inside the JAR must be of the pattern {@code /native/{os}/{arch}/{object name}}
     * where {@code os} is either "windows" or "linux", {@code arch} is the system architecture as in
     * the system property {@code os.arch} and {@code object name} is the name of the native object
     * (e.g. "discord_game_sdk_jni.dll" on Windows or "libdiscord_game_sdk_jni.so" on Linux.
     * <p>
     * You may call this method more than once which unloads the old shared object and loads the new one.
     * <p>
     * The resource will be read and copied into a temporary file.
     *
     * @throws UnsatisfiedLinkError if Discord's native library can not be loaded
     */
    public static void initFromClasspath() {
        // Find out which name Discord's library has (.dll for Windows, .so for Linux)
        String name = "discord_game_sdk";
        String suffix;

        String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        String arch = System.getProperty("os.arch").toLowerCase(Locale.ROOT);

        if (osName.contains("windows")) {
            suffix = ".dll";
        } else if (osName.contains("linux")) {
            suffix = ".so";
        } else if (osName.contains("mac os")) {
            suffix = ".dylib";
        } else {
            throw new RuntimeException("cannot determine OS type: " + osName);
        }

        /*
        Some systems report "amd64" (e.g. Windows and Linux), some "x86_64" (e.g. Mac OS).
        At this point we need the "x86_64" version, as this one is used in the ZIP.
         */
        if (arch.equals("amd64")) arch = "x86_64";

        // Path of Discord's library inside the ZIP
        String res = "/lib/" + arch + "/" + name + suffix;

        Core.init(Objects.requireNonNull(Core.class.getResource(res)));
    }

    private static File downloadDiscordLibrary() throws IOException {
        // Find out which name Discord's library has (.dll for Windows, .so for Linux)
        String name = "discord_game_sdk";
        String suffix;

        String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
        String arch = System.getProperty("os.arch").toLowerCase(Locale.ROOT);

        if (osName.contains("windows")) {
            suffix = ".dll";
        } else if (osName.contains("linux")) {
            suffix = ".so";
        } else if (osName.contains("mac os")) {
            suffix = ".dylib";
        } else {
            throw new RuntimeException("cannot determine OS type: " + osName);
        }

        /*
        Some systems report "amd64" (e.g. Windows and Linux), some "x86_64" (e.g. Mac OS).
        At this point we need the "x86_64" version, as this one is used in the ZIP.
         */
        if (arch.equals("amd64")) arch = "x86_64";

        // Path of Discord's library inside the ZIP
        String zipPath = "lib/" + arch + "/" + name + suffix;

        // Open the URL as a ZipInputStream
        URL downloadUrl = new URL("https://dl-game-sdk.discordapp.net/2.5.6/discord_game_sdk.zip");
        HttpURLConnection connection = (HttpURLConnection) downloadUrl.openConnection();
        connection.setRequestProperty(
                "User-Agent", "discord-game-sdk4j (https://github.com/JnCrMx/discord-game-sdk4j)");
        ZipInputStream zin = new ZipInputStream(connection.getInputStream());

        // Search for the right file inside the ZIP
        ZipEntry entry;
        while ((entry = zin.getNextEntry()) != null) {
            if (entry.getName().equals(zipPath)) {
                // Create a new temporary directory
                // We need to do this, because we may not change the filename on Windows
                File tempDir = new File(System.getProperty("java.io.tmpdir"), "java-" + name + System.nanoTime());
                if (!tempDir.mkdir()) throw new IOException("Cannot create temporary directory");
                tempDir.deleteOnExit();

                // Create a temporary file inside our directory (with a "normal" name)
                File temp = new File(tempDir, name + suffix);
                temp.deleteOnExit();

                // Copy the file in the ZIP to our temporary file
                Files.copy(zin, temp.toPath());

                // We are done, so close the input stream
                zin.close();

                // Return our temporary file
                return temp;
            }
            // next entry
            zin.closeEntry();
        }
        zin.close();
        // We couldn't find the library inside the ZIP
        return null;
    }

    /**
     * Extracts and initializes the native library.
     * This method also downloads, extracts and loads Discord's native library from
     * <a href="https://dl-game-sdk.discordapp.net/2.5.6/discord_game_sdk.zip">
     *     https://dl-game-sdk.discordapp.net/2.5.6/discord_game_sdk.zip</a>.
     * <p>
     * The JNI library is extracted from the classpath (e.g. the currently running JAR)
     * using {@link Class#getResourceAsStream(String)}.
     * Its path inside the JAR must be of the pattern {@code /native/{os}/{arch}/{object name}}
     * where {@code os} is either "windows" or "linux", {@code arch} is the system architecture as in
     * the system property {@code os.arch} and {@code object name} is the name of the native object
     * (e.g. "discord_game_sdk_jni.dll" on Windows or "libdiscord_game_sdk_jni.so" on Linux.
     * <p>
     * You may call this method more than once which unloads the old shared object and loads the new one.
     * <p>
     * The resource will be read and copied into a temporary file.
     *
     * @throws UnsatisfiedLinkError if Discord's native library can not be loaded
     */
    public static void initDownload() throws IOException {
        File f = downloadDiscordLibrary();
        if (f == null) throw new FileNotFoundException("cannot find native library in downloaded zip file");
        init(f);
    }

    /**
     * Loads Discord's SDK library.
     * <p>
     * This does not extract nor load the JNI native library.
     * If you want to do that, please use {@link Core#init(File)}
     * which extracts and loads the JNI native and then calls this method.
     * @param discordPath Location of Discord's native library.
     *                    <p>On Windows the filename (last component of the path) must be
     *                    "discord_game_sdk.dll" or an {@link UnsatisfiedLinkError} will occur.</p>
     *                    <p>On Linux the filename does not matter.</p>
     */
    public static native void initDiscordNative(String discordPath);

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
     * sets the log hook to {@link Core#DEFAULT_LOG_HOOK}.
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
    public Core(CreateParams params) {
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

    private native long getUserManager(long pointer);

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
     * @see Core#DEFAULT_LOG_HOOK
     * @see <a href="https://discordapp.com/developers/docs/game-sdk/discord#setloghook">
     *     https://discordapp.com/developers/docs/game-sdk/discord#setloghook</a>
     */
    public void setLogHook(LogLevel minLevel, BiConsumer<LogLevel, String> logHook) {
        execute(() -> setLogHook(pointer, minLevel.ordinal(), Objects.requireNonNull(logHook)));
    }

    /**
     * Returns true if this {@link Core} instance is open, i.e. {@link #close()} has not
     * been called yet. Calling certain SDK methods will throw {@link CoreClosedException}
     * if the {@link Core} is not open.
     * @return True if this {@link Core} is still open, false otherwise
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

    void execute(Runnable runnable) {
        execute((Supplier<Void>) () -> {
            runnable.run();
            return null;
        });
    }

    <T> T execute(Supplier<T> provider) {
        if (!isOpen()) throw new CoreClosedException();

        lock.lock();
        try {
            return provider.get();
        } finally {
            lock.unlock();
        }
    }
}
