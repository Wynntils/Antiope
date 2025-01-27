set(CMAKE_SYSTEM_NAME macOS)
LIST(APPEND CMAKE_PROGRAM_PATH ~/osxcross/target/bin)

set(CMAKE_C_FLAGS -fuse-ld=/home/ryan/osxcross/target/bin/x86_64-apple-darwin20.4-ld)

set(CMAKE_C_COMPILER o64-clang)

set(CMAKE_SHARED_LIBRARY_SUFFIX_C ".dylib")
set(JAVA_INCLUDE_PATH /usr/lib/jvm/macos-x64-jdk-11.0.19/Contents/Home/include)
set(JAVA_INCLUDE_PATH2 /usr/lib/jvm/macos-x64-jdk-11.0.19/Contents/Home/include/darwin)
set(JAVA_AWT_INCLUDE_PATH NotNeeded)
set(JAVA_AWT_LIBRARY NotNeeded)
set(JAVA_JVM_LIBRARY /usr/lib/jvm/macos-x64-jdk-11.0.19/Contents/Home)
