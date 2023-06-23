#include <discord_game_sdk.h>

#include "com_wynntils_antiope_manager_activity_type_ActivityTimestamps.h"

JNIEXPORT void JNICALL Java_com_wynntils_antiope_manager_activity_type_ActivityTimestamps_setStart(JNIEnv *env, jobject object, jlong pointer, jlong start)
{
	struct DiscordActivityTimestamps *timestamps = (struct DiscordActivityTimestamps*) pointer;
	timestamps->start = start;
}

JNIEXPORT jlong JNICALL Java_com_wynntils_antiope_manager_activity_type_ActivityTimestamps_getStart(JNIEnv *env, jobject object, jlong pointer)
{
	struct DiscordActivityTimestamps *timestamps = (struct DiscordActivityTimestamps*) pointer;
	return timestamps->start;
}

JNIEXPORT void JNICALL Java_com_wynntils_antiope_manager_activity_type_ActivityTimestamps_setEnd(JNIEnv *env, jobject object, jlong pointer, jlong end)
{
	struct DiscordActivityTimestamps *timestamps = (struct DiscordActivityTimestamps*) pointer;
	timestamps->end = end;
}

JNIEXPORT jlong JNICALL Java_com_wynntils_antiope_manager_activity_type_ActivityTimestamps_getEnd(JNIEnv *env, jobject object, jlong pointer)
{
	struct DiscordActivityTimestamps *timestamps = (struct DiscordActivityTimestamps*) pointer;
	return timestamps->end;
}
