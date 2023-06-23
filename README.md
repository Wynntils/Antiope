# Antiope

## Details
This project provides Java bindings for the rich presence portion of the
[Discord GameSDK](https://discordapp.com/developers/docs/game-sdk/sdk-starter-guide).

The intent of this fork is to provide rich presence support for [Wynntils/Artemis](https://github.com/Wynntils/artemis).
Changes from the original project include proper documentation for building native libraries from source, as well as macOS ARM support along with code refactorings.

## Rich Presence

If you are just looking for an alternative to the deprecated [Discord Rich Presence SDK](https://discord.com/developers/docs/rich-presence/how-to),
check out [ActivityManager](https://github.com/Wynntils/Antiope/blob/master/src/main/java/com/wynntils/antiope/manager/activity/ActivityManager.java).

If you are using this as a part of a Minecraft mod, it is highly recommended that you run callbacks in a TickEvent or in a separate thread.

## Installation 

### Pre-compiled

#### Maven, Gradle and other build tools

There are pre-compiled builds on Github Packages.

#### Manual installation

For projects not using any build tools, download a pre-compiled JAR-file (``antiope-<version>.jar``)
from the [releases page](https://github.com/JnCrMx/discord-game-sdk4j/releases).

If you want, you can also download the sources (``antiope-<version>-sources.jar``).

You do **not** need to download the .dll or .so files! They are packed in the JAR and will be automatically extracted.

After downloading those JARs, just add the main JAR to your project's classpath.

### Building from source

To obtain the native libraries you can build them from source too (see below) or just download them [here](https://dl-game-sdk.discordapp.net/3.2.1/discord_game_sdk.zip).
For the Windows files, you should rename them to `discord_game_sdk_jni.dll` and place them under `src/main/resources/native/windows/amd64` and `src/main/resources/native/windows/x86` respectively. 
For macOS, you should rename the file to `libdiscord_game_sdk_jni.dylib` and place it under `src/main/resources/native/macosx/amd64`.
For Linux, you should rename the file to `libdiscord_game_sdk_jni.so` and place it under `src/main/resources/native/linux/amd64`.

Finally, build (and install) the library with Maven:
```shell
mvn clean install -Dmaven.antrun.skip=true
```

## Building the native library from source
### This is required for any changes to the C code.

This guide is for WSL (Ubuntu) and it also probably works on normal Linux. It definitely does not work on macOS/Windows.

Start by installing a lot of dependencies:
```shell
sudo apt update
sudo apt install -y clang cmake make libssl-dev liblzma-dev lzma-dev libxml2-dev libbz2-dev
sudo apt install -y g++-mingw-w64-i686 g++-mingw-w64-x86-64 gcc-mingw-w64-i686 gcc-mingw-w64-x86-64 openjdk-11-jdk
```

And also download [Maven](https://maven.apache.org/download.cgi) if you don't have it. Extract it somewhere and add the `bin` folder to your `PATH`.
It is possible to download Maven to the host Windows machine and add it to Windows' `PATH`.

Lastly, note that you can access Windows host machine files from WSL at `/mnt/c/` (or any other drive letter).

At this point, you should have folder `/usr/lib/jvm` with some copies of your Linux JDK.
Ensure your `JAVA_HOME` is pointed to the correct directory (probably some variation of `/usr/lib/jvm/java-11-openjdk-amd64/`).
You can check with `echo ${JAVA_HOME}`.
If it's not there, you can run `export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64`.

#### Prepare for Windows cross-compilation

You will need to download some copy of OpenJDK 11 for Windows. The compressed archive version, not the installer.
Unzip the archive and place it in `/usr/lib/jvm`. You should rename the folder to `windows-x64-jdk-11.0.19`, or whatever version you have.

Next, go to the `toolchains` folder in this project directory. You may need to edit both `windows-x86.cmake` and `windows-amd64.cmake`.
At the end of each file, there are two lines:
```
set(JAVA_INCLUDE_PATH /usr/lib/jvm/windows-x64-jdk-11.0.19/include/)
set(JAVA_INCLUDE_PATH2 /usr/lib/jvm/windows-x64-jdk-11.0.19/include/win32/)
```
Ensure that the paths are correct. They should be pointing to the `include` and `include/win32` folders in your Windows JDK.
Note that it does have to point to the `/include/` subdirectory as shown.

#### Prepare for macOS cross-compilation

For macOS, this part is a bit more complicated. 
You will need to follow the instructions [here](https://github.com/tpoechtrager/osxcross#packaging-the-sdk), but they are slightly wrong. 
In `~`, clone the repository.

You should be using the **"Packing the SDK on Linux - Method 1 (Xcode > 8.0)"** option.
Xcode version 12.5.1 is tested and working. Version 14.3.1 and newer does **not** work.
**You can skip step 2.** We have installed the correct dependencies already, and the ones listed here are wrong.

Once you do the above, you can proceed to their [installation instructions](https://github.com/tpoechtrager/osxcross#installation).
Again, note that the packages mentioned are wrong. You should exclude `xz` and `libbz2`. **Do not run their `get_dependencies.sh` script, it does not work.**
You can run `./build.sh` immediately after manually ensuring that the dependencies are installed.

Lastly, you will have to edit `macos-amd64.cmake` and change the paths (lines 2 and 4) to point to your installation.
Note that line 4 does not allow you to use `~`, so you will have to use the full path.

#### Build the native library

Finally, download [Discord's native library](https://discord.com/developers/docs/game-sdk/sdk-starter-guide)
and extract it to ``./discord_game_sdk/``. You should be using v3.2.1 (or other compatible version) as v2.5.6 does not have ARM support.

The CMake build system is integrated in Maven, so just execute the following command.
```shell script
mvn clean install
```
The output dll/so/dylib files will be placed in the `target/native` directory. For convenience, you can run `copy-natives.sh` to copy them to `src/main/resources/native`.

## Usage

In code, the first step is initializing the Core. To do this you need to pass the path to Discord's native library as an argument.
You can find this library in the directory you just extracted the ZIP file at ``lib/x86_64/discord_game_sdk.dll`` (for 64-bit Windows)
and ``lib/x86_64/discord_game_sdk.so`` (for 64-bit Linux):

```java
DiscordGameSDKCore.loadLibrary();
```

Now you are ready to use the library!

````java
class Example {
    public static void main(String[] args) {
        try {
            CreateParams params = new CreateParams();
            params.setClientID(APPLICATION_ID_AS_LONG);
            params.setFlags(CreateParams.getDefaultFlags());

            DiscordGameSDKCore core = new DiscordGameSDKCore(params);
            try {
                // do something with your Core
            }
        }
    }
    
}
````