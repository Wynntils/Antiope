set(CMAKE_SYSTEM_NAME macOS)
LIST(APPEND CMAKE_PROGRAM_PATH ~/osxcross/target/bin)

set(CMAKE_C_FLAGS -fuse-ld=/home/ryan/osxcross/target/bin/arm64-apple-darwin20.4-ld)

set(CMAKE_C_COMPILER o64-clang)

set(CMAKE_SHARED_LIBRARY_SUFFIX_C ".dylib")
set(JAVA_INCLUDE_PATH /usr/lib/jvm/macos-x64-jdk-11.0.19/Contents/Home/include)
