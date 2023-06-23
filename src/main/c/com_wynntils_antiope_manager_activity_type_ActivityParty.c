#include <discord_game_sdk.h>

#include "com_wynntils_antiope_manager_activity_type_ActivityParty.h"

JNIEXPORT void JNICALL Java_com_wynntils_antiope_manager_activity_type_ActivityParty_setID(JNIEnv *env, jobject object, jlong pointer, jstring id)
{
	struct DiscordActivityParty *party = (struct DiscordActivityParty*) pointer;
	
	const char *nativeString = (*env)->GetStringUTFChars(env, id, 0);
	strcpy(party->id, nativeString);
	(*env)->ReleaseStringUTFChars(env, id, nativeString);
}

JNIEXPORT jstring JNICALL Java_com_wynntils_antiope_manager_activity_type_ActivityParty_getID(JNIEnv *env, jobject object, jlong pointer)
{
	struct DiscordActivityParty *party = (struct DiscordActivityParty*) pointer;
	
	return (*env)->NewStringUTF(env, party->id);
}

JNIEXPORT jlong JNICALL Java_com_wynntils_antiope_manager_activity_type_ActivityParty_getSize(JNIEnv *env, jobject object, jlong pointer)
{
	struct DiscordActivityParty *party = (struct DiscordActivityParty*) pointer;
	return (uint64_t) &(party->size);
}
