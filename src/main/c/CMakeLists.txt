cmake_minimum_required(VERSION 3.4.1)

project(discord_game_sdk_jni)

find_package(JNI REQUIRED)
include_directories(${JNI_INCLUDE_DIRS})
include_directories(../../../discord_game_sdk/c)
include_directories(${JAVA_INCLUDE_PATH})
include_directories(${JAVA_INCLUDE_PATH2})

add_library(discord_game_sdk_jni

			SHARED

			com_wynntils_antiope_activity_Activity.c
			com_wynntils_antiope_activity_ActivityAssets.c
			com_wynntils_antiope_activity_ActivityParty.c
			com_wynntils_antiope_activity_ActivityPartySize.c
			com_wynntils_antiope_activity_ActivitySecrets.c
			com_wynntils_antiope_activity_ActivityTimestamps.c
			com_wynntils_antiope_ActivityManager.c
			com_wynntils_antiope_CreateParams.c
			com_wynntils_antiope_Core.c
			com_wynntils_antiope_OverlayManager.c

			Callback.c
			EventHandler.c)

if(UNIX)
	target_link_libraries(discord_game_sdk_jni dl)
endif(UNIX)

if(MSVC)
	add_library(discord_game_sdk SHARED IMPORTED)
	set_target_properties(discord_game_sdk
		PROPERTIES
		IMPORTED_IMPLIB
		${CMAKE_SOURCE_DIR}/../../../discord_game_sdk/lib/${BUILDARCH}/discord_game_sdk.dll.lib)
	target_link_libraries(discord_game_sdk_jni discord_game_sdk)
	set_target_properties(discord_game_sdk_jni PROPERTIES PREFIX "")
endif(MSVC)
