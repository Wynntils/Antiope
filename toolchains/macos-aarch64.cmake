set(CMAKE_SYSTEM_NAME macOS)
set(CMAKE_SYSTEM_PROCESSOR arm64)
LIST(APPEND CMAKE_PROGRAM_PATH ~/osxcross/target/bin)

set(CMAKE_C_COMPILER /home/ryan/osxcross/target/bin/oa64-clang)
set(CMAKE_C_COMPILER_TARGET arm64-apple-darwin20.4)
set(CMAKE_C_FLAGS -fuse-ld=/home/ryan/osxcross/target/bin/arm64-apple-darwin20.4-ld)

set(CMAKE_SHARED_LIBRARY_SUFFIX_C ".dylib")
set(JAVA_INCLUDE_PATH /usr/lib/jvm/macos-aarch64-jdk-11.0.19/Contents/Home/include)
set(JAVA_INCLUDE_PATH2 /usr/lib/jvm/macos-aarch64-jdk-11.0.19/Contents/Home/include/darwin)
set(JAVA_AWT_INCLUDE_PATH NotNeeded)
set(JAVA_AWT_LIBRARY NotNeeded)
set(JAVA_JVM_LIBRARY /usr/lib/jvm/macos-aarch64-jdk-11.0.19/Contents/Home)
