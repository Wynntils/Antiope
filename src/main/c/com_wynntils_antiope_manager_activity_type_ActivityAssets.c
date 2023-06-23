#include <discord_game_sdk.h>

#include "com_wynntils_antiope_manager_activity_type_ActivityAssets.h"

JNIEXPORT void JNICALL Java_com_wynntils_antiope_manager_activity_type_ActivityAssets_setLargeImage(JNIEnv *env, jobject object, jlong pointer, jstring asset_key)
{
	struct DiscordActivityAssets *assets = (struct DiscordActivityAssets*) pointer;
	
	const char *nativeString = (*env)->GetStringUTFChars(env, asset_key, 0);
	strcpy(assets->large_image, nativeString);
	(*env)->ReleaseStringUTFChars(env, asset_key, nativeString);
}

JNIEXPORT jstring JNICALL Java_com_wynntils_antiope_manager_activity_type_ActivityAssets_getLargeImage(JNIEnv *env, jobject object, jlong pointer)
{
	struct DiscordActivityAssets *assets = (struct DiscordActivityAssets*) pointer;
	
	return (*env)->NewStringUTF(env, assets->large_image);
}

JNIEXPORT void JNICALL Java_com_wynntils_antiope_manager_activity_type_ActivityAssets_setLargeText(JNIEnv *env, jobject object, jlong pointer, jstring text)
{
	struct DiscordActivityAssets *assets = (struct DiscordActivityAssets*) pointer;
	
	const char *nativeString = (*env)->GetStringUTFChars(env, text, 0);
	strcpy(assets->large_text, nativeString);
	(*env)->ReleaseStringUTFChars(env, text, nativeString);
}

JNIEXPORT jstring JNICALL Java_com_wynntils_antiope_manager_activity_type_ActivityAssets_getLargeText(JNIEnv *env, jobject object, jlong pointer)
{
	struct DiscordActivityAssets *assets = (struct DiscordActivityAssets*) pointer;
	
	return (*env)->NewStringUTF(env, assets->large_text);
}


JNIEXPORT void JNICALL Java_com_wynntils_antiope_manager_activity_type_ActivityAssets_setSmallImage(JNIEnv *env, jobject object, jlong pointer, jstring asset_key)
{
	struct DiscordActivityAssets *assets = (struct DiscordActivityAssets*) pointer;
	
	const char *nativeString = (*env)->GetStringUTFChars(env, asset_key, 0);
	strcpy(assets->small_image, nativeString);
	(*env)->ReleaseStringUTFChars(env, asset_key, nativeString);
}

JNIEXPORT jstring JNICALL Java_com_wynntils_antiope_manager_activity_type_ActivityAssets_getSmallImage(JNIEnv *env, jobject object, jlong pointer)
{
	struct DiscordActivityAssets *assets = (struct DiscordActivityAssets*) pointer;
	
	return (*env)->NewStringUTF(env, assets->small_image);
}

JNIEXPORT void JNICALL Java_com_wynntils_antiope_manager_activity_type_ActivityAssets_setSmallText(JNIEnv *env, jobject object, jlong pointer, jstring text)
{
	struct DiscordActivityAssets *assets = (struct DiscordActivityAssets*) pointer;
	
	const char *nativeString = (*env)->GetStringUTFChars(env, text, 0);
	strcpy(assets->small_text, nativeString);
	(*env)->ReleaseStringUTFChars(env, text, nativeString);
}

JNIEXPORT jstring JNICALL Java_com_wynntils_antiope_manager_activity_type_ActivityAssets_getSmallText(JNIEnv *env, jobject object, jlong pointer)
{
	struct DiscordActivityAssets *assets = (struct DiscordActivityAssets*) pointer;
	
	return (*env)->NewStringUTF(env, assets->small_text);
}
