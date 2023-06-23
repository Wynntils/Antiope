# discord-game-sdk4j

[![](https://jitpack.io/v/JnCrMx/discord-game-sdk4j.svg)](https://jitpack.io/#JnCrMx/discord-game-sdk4j)

This project provides Java bindings for the
[Discord GameSDK](https://discordapp.com/developers/docs/game-sdk/sdk-starter-guide).

To be honest I'm not sure if people even need this, because Discord apparently discarded its game store idea.

But maybe the activity, overlay, user, and relationship features could be useful to some people.

## Rich Presence

If you are just looking for an alternative to the deprecated [Discord Rich Presence SDK](https://discord.com/developers/docs/rich-presence/how-to),
head over to the [ActivityExample.java](examples/ActivityExample.java)!

## Features of the SDK

**Some of the features are deprecated by Discord as of Wed, 09 Nov 2022 and will be decommissioned and stop working on Tuesday May 2, 2023.
They are marked with :broken_heart: in the table.
Those already implemented will most likely continue to work until Discord decommissions them.
Those not implemented will remains such, as putting work into features which will end working in less than a year does not seem worth it to me.**

| Feature                                                                     | State                                         | Example                                                                                                                                  |
|-----------------------------------------------------------------------------|-----------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------|
| [Achievements](https://discord.com/developers/docs/game-sdk/achievements)   | :x: not implemented :broken_heart:            |                                                                                                                                          |
| [Activities](https://discord.com/developers/docs/game-sdk/activities)       | :heavy_check_mark: implemented                | [ActivityExample.java](examples/ActivityExample.java)                                                                                    |
| [Applications](https://discord.com/developers/docs/game-sdk/applications)   | :x: not implemented :broken_heart:            |                                                                                                                                          |
| [Voice](https://discord.com/developers/docs/game-sdk/discord-voice)         | :heavy_check_mark: implemented :broken_heart: | [VoiceExample.java](examples/VoiceExample.java)                                                                                          |
| [Images](https://discord.com/developers/docs/game-sdk/images)               | :heavy_check_mark: implemented :broken_heart: | none yet :cry: (see [``imageTest()``](src/test/java/de/jcm/discordgamesdk/DiscordTest.java#L417) for now)                                |
| [Lobbies](https://discord.com/developers/docs/game-sdk/lobbies)             | :heavy_check_mark: implemented :broken_heart: | [LobbyExample.java](examples/LobbyExample.java)                                                                                          |
| [Networking](https://discord.com/developers/docs/game-sdk/networking)       | :heavy_check_mark: implemented :broken_heart: | [NetworkExample.java](examples/NetworkExample.java)                                                                                      |
| [Overlay](https://discord.com/developers/docs/game-sdk/overlay)             | :heavy_check_mark: implemented                | none yet :cry: (see [``overlayTest()``](src/test/java/de/jcm/discordgamesdk/DiscordTest.java#L289) for now)                              |
| [Relationships](https://discord.com/developers/docs/game-sdk/relationships) | :heavy_check_mark: implemented                | [RelationshipExample.java](examples/RelationshipExample.java), [FriendNotificationExample.java](examples/FriendNotificationExample.java) |
| [Storage](https://discord.com/developers/docs/game-sdk/storage)             | :x: not implemented :broken_heart:            |                                                                                                                                          |
| [Store](https://discord.com/developers/docs/game-sdk/store)                 | :x: not implemented :broken_heart:            |                                                                                                                                          |
| [Users](https://discord.com/developers/docs/game-sdk/users)                 | :heavy_check_mark: implemented                | none yet :cry: (see [``userTest()``](src/test/java/de/jcm/discordgamesdk/DiscordTest.java#L216) for now)                                 |

I will try to work on features that are not implemented yet soon,
but the remaining ones are quite difficult to test,
so I don't know how much progress I can make on them.

## Installation 

### Pre-compiled

#### Maven, Gradle and other build tools

There are pre-compiled builds on [JitPack](https://jitpack.io/#JnCrMx/discord-game-sdk4j)
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
For MacOS, you should rename the file to `libdiscord_game_sdk_jni.dylib` and place it under `src/main/resources/native/macosx/amd64`.
For Linux, you should rename the file to `libdiscord_game_sdk_jni.so` and place it under `src/main/resources/native/linux/amd64`.

Finally, build (and install) the library with Maven:
```shell
mvn clean install -Dmaven.antrun.skip=true
```

If you want to skip the tests (sometimes they fail for really weird reasons), add ``-DskipTests`` to the command arguments.

### Building the native library from source (does not work for MacOS yet)

So this will be a rather tedious process. This guide is for WSL (Ubuntu). Definitely does not work on MacOS/Windows.

Start by installing a lot of dependencies:
```shell
sudo apt update
sudo apt install -y cmake g++-mingw-w64-i686 g++-mingw-w64-x86-64 gcc-mingw-w64-i686 gcc-mingw-w64-x86-64 openjdk-11-jdk
```

And also download [Maven](https://maven.apache.org/download.cgi) if you don't have it. Extract it somewhere and add the `bin` folder to your `PATH`.

You will also need to download some copy of OpenJDK 11 for Windows. The compressed archive version, not the installer.

At this point, you should have folder `/usr/lib/jvm` with some copies of your Linux JDK. Unzip and move your Windows copy here.
Then, ensure your `JAVA_HOME` is point to the correct directory (probably some variation of `/usr/lib/jvm/java-11-openjdk-amd64/`).
If it's not there, you can run `export JAVA_HOME=/usr/lib/whatever`.

Then download [Discord's native library](https://discord.com/developers/docs/game-sdk/sdk-starter-guide)
and extract it to ``./discord_game_sdk/``. You can try with v3.2.1 first and switch to v2.5.6 if it doesn't work.

Next, go to the `toolchains` folder in this project directory. You can ignore the `linux-amd64.cmake` file since we're running on WSL.
However, you'll need to edit both `windows-x86.cmake` and `windows-amd64.cmake`. In the bottom of these files, there are two lines:
```
set(JAVA_INCLUDE_PATH /usr/lib/jvm/windows-x64-jdk-11.0.19/include/)
set(JAVA_INCLUDE_PATH2 /usr/lib/jvm/windows-x64-jdk-11.0.19/include/win32/)
```

Make sure these paths point to the Windows JDK you extracted earlier. Note that it does have to point to the `/include/` subdirectory as shown.

The CMake build system is integrated in Maven, so just execute to following command to
build and install the Java and native library:

```shell script
mvn clean install
```

I think that was it. If it doesn't work for you, please open an issue. I might have forgotten something as it took a few hours to figure this out without docs.

## Usage

To use the library, you first need to download [Discord's native library](https://dl-game-sdk.discordapp.net/2.5.6/discord_game_sdk.zip).
Extract the ZIP file and remember where you put it.

In code the first step is initializing the Core. To do this you need to pass the path to Discord's native library as an argument.
You can find this library in the directory you just extracted the ZIP file at ``lib/x86_64/discord_game_sdk.dll`` (for 64-bit Windows)
and ``lib/x86_64/discord_game_sdk.so`` (for 64-bit Linux):

```java
Core.init(new File("<path to the native library>"));
```

Now you are ready to use the library!

````java
try(CreateParams params = new CreateParams())
{
    params.setClientID(<your application ID as a long>);
    params.setFlags(CreateParams.getDefaultFlags());

    try(Core core = new Core(params))
    {
        // do something with your Core
    }
}
````

For real examples see the ``examples/`` directory in this repository.
