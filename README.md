# discord-game-sdk4j

This project provides Java bindings for the rich presence portion of the
[Discord GameSDK](https://discordapp.com/developers/docs/game-sdk/sdk-starter-guide).

The intent of this fork is to provide rich presence support for [Wynntils/Artemis](https://github.com/Wynntils/artemis).
Changes from the original project include proper documentation for building native libraries from source, as well as macOS ARM support.

## Rich Presence

If you are just looking for an alternative to the deprecated [Discord Rich Presence SDK](https://discord.com/developers/docs/rich-presence/how-to),
head over to the [ActivityExample.java](examples/ActivityExample.java)!

If you are using this as a part of a Minecraft mod, it is highly recommended that you instead put the callback in a TickEvent, instead of a while true loop.

## Installation 

### Pre-compiled

#### Maven, Gradle and other build tools

There are pre-compiled builds on JitPack (link TBD)
together with instructions how to use them for all common build tools.

#### Manual installation

For projects not using any build tools, download a pre-compiled JAR-file (``discord-game-sdk4j-<version>.jar``)
from the [releases page](https://github.com/JnCrMx/discord-game-sdk4j/releases).

If you want, you can also download the JavaDocs (``discord-game-sdk4j-<version>-javadoc.jar``) or
the sources (``discord-game-sdk4j-<version>-sources.jar``).

You do **not** need to download the .dll or .so files! They are packed in the JAR and will be automatically extracted.

After downloading those JARs, just add the main JAR to your project's classpath and optionally
attach sources or JavaDocs.

### Building from source

To obtain the native libraries you can build them from source too (see below) or just download them [here](https://github.com/JnCrMx/discord-game-sdk4j/releases/tag/v0.5.5).
For the Windows files, you should rename them to `discord_game_sdk_jni.dll` and place them under `src/main/resources/native/windows/amd64`
and `src/main/resources/native/windows/x86` respectively. 
For macOS, you should rename the file to `libdiscord_game_sdk_jni.dylib` and place it under `src/main/resources/native/macosx/amd64`.
For Linux, you should rename the file to `libdiscord_game_sdk_jni.so` and place it under `src/main/resources/native/linux/amd64`.

Finally, build (and install) the library with Maven:
```shell
mvn clean install -Dmaven.antrun.skip=true
```

### Building the native library from source (does not work for MacOS yet)

So this will be a rather tedious process. This guide is for WSL (Ubuntu). Probably works on normal Linux. Definitely does not work on macOS/Windows.

Start by installing a lot of dependencies:
```shell
sudo apt update
sudo apt install -y cmake g++-mingw-w64-i686 g++-mingw-w64-x86-64 gcc-mingw-w64-i686 gcc-mingw-w64-x86-64 openjdk-11-jdk
```

And also download [Maven](https://maven.apache.org/download.cgi) if you don't have it. Extract it somewhere and add the `bin` folder to your `PATH`.

You will also need to download some copy of OpenJDK 11 for Windows. The compressed archive version, not the installer.

At this point, you should have folder `/usr/lib/jvm` with some copies of your Linux JDK. Unzip and move your Windows copy here.
Then, ensure your `JAVA_HOME` is point to the correct directory (probably some variation of `/usr/lib/jvm/java-11-openjdk-amd64/`).
You can check with `echo ${JAVA_HOME}`.
If it's not there, you can run `export JAVA_HOME=/usr/lib/whatever`.

For macOS, this part is a bit more complicated. You will need to follow the instructions [here](https://github.com/tpoechtrager/osxcross#packaging-the-sdk),
but they are slightly wrong. In `~`, clone the repository.
We are going to be using the **"Packing the SDK on Linux - Method 1 (Xcode > 8.0)"** option.
The Xcode version I tested was 12.5.1. 14.3.1 did **not** seem to work. 
Also note that step 2 is incorrect. Instead of installing `libssl-devel lzma-devel libxml2-devel`, you will need
`libssl-dev liblzma-dev lzma-dev libxml2-dev libbz2-dev`.

Once you do the above, you can proceed to their [installation instructions](https://github.com/tpoechtrager/osxcross#installation).
Again, note that the packages mentioned are wrong. You should exclude `xz` and `libbz2`. 
Then, running `./build.sh` worked for me.
Lastly, you will have to edit `macos-amd64.cmake` and change the paths (lines 2 and 4) to point to your installation.
Note that line 4 does not allow you to use `~`, so you will have to use the full path.

Next, go to the `toolchains` folder in this project directory. You can ignore the `linux-amd64.cmake` file since we're running on WSL.
However, you'll need to edit both `windows-x86.cmake` and `windows-amd64.cmake`. In the bottom of these files, there are two lines:
```
set(JAVA_INCLUDE_PATH /usr/lib/jvm/windows-x64-jdk-11.0.19/include/)
set(JAVA_INCLUDE_PATH2 /usr/lib/jvm/windows-x64-jdk-11.0.19/include/win32/)
```

Make sure these paths point to the Windows JDK you extracted earlier. Note that it does have to point to the `/include/` subdirectory as shown.

Finally, download [Discord's native library](https://discord.com/developers/docs/game-sdk/sdk-starter-guide)
and extract it to ``./discord_game_sdk/``. You can try with v3.2.1 first and switch to v2.5.6 if it doesn't work.

The CMake build system is integrated in Maven, so just execute to following command to
build and install the Java and native library:

```shell script
mvn clean install
```

I think that was it. If it doesn't work for you, please open an issue. I might have forgotten something as it took a few hours to figure this out without docs.

## Usage

To use the library, you first need to download [Discord's native library](https://discord.com/developers/docs/game-sdk/sdk-starter-guide).
Extract the ZIP file and remember where you put it. You should use v3.2.1 as v2.5.6 does not have ARM support.

In code the first step is initializing the Core. To do this you need to pass the path to Discord's native library as an argument.
You can find this library in the directory you just extracted the ZIP file at ``lib/x86_64/discord_game_sdk.dll`` (for 64-bit Windows)
and ``lib/x86_64/discord_game_sdk.so`` (for 64-bit Linux):

```java
Core.init(new File("<path to the native library>"));
```

Now you are ready to use the library!

````java
class Example {
    public static void main(String[] args) {
        try {
            CreateParams params = new CreateParams();
            params.setClientID(APPLICATION_ID_AS_LONG);
            params.setFlags(CreateParams.getDefaultFlags());

            Core core = new Core(params);
            try {
                // do something with your Core
            }
        }
    }
    
}
````

For real examples see the ``examples/`` directory in this repository.
