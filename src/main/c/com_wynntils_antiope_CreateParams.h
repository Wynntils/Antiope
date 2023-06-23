/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_wynntils_antiope_CreateParams */

#ifndef _Included_com_wynntils_antiope_CreateParams
#define _Included_com_wynntils_antiope_CreateParams
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_wynntils_antiope_CreateParams
 * Method:    allocate
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_com_wynntils_antiope_CreateParams_allocate
  (JNIEnv *, jobject);

/*
 * Class:     com_wynntils_antiope_CreateParams
 * Method:    free
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_wynntils_antiope_CreateParams_free
  (JNIEnv *, jobject, jlong);

/*
 * Class:     com_wynntils_antiope_CreateParams
 * Method:    setClientID
 * Signature: (JJ)V
 */
JNIEXPORT void JNICALL Java_com_wynntils_antiope_CreateParams_setClientID
  (JNIEnv *, jobject, jlong, jlong);

/*
 * Class:     com_wynntils_antiope_CreateParams
 * Method:    getClientID
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_com_wynntils_antiope_CreateParams_getClientID
  (JNIEnv *, jobject, jlong);

/*
 * Class:     com_wynntils_antiope_CreateParams
 * Method:    setFlags
 * Signature: (JJ)V
 */
JNIEXPORT void JNICALL Java_com_wynntils_antiope_CreateParams_setFlags
  (JNIEnv *, jobject, jlong, jlong);

/*
 * Class:     com_wynntils_antiope_CreateParams
 * Method:    getFlags
 * Signature: (J)J
 */
JNIEXPORT jlong JNICALL Java_com_wynntils_antiope_CreateParams_getFlags
  (JNIEnv *, jobject, jlong);

/*
 * Class:     com_wynntils_antiope_CreateParams
 * Method:    registerEventHandler
 * Signature: (JLde/jcm/discordgamesdk/events/EventHandler;)V
 */
JNIEXPORT void JNICALL Java_com_wynntils_antiope_CreateParams_registerEventHandler
  (JNIEnv *, jobject, jlong, jobject);

/*
 * Class:     com_wynntils_antiope_CreateParams
 * Method:    getDefaultFlags
 * Signature: ()J
 */
JNIEXPORT jlong JNICALL Java_com_wynntils_antiope_CreateParams_getDefaultFlags
  (JNIEnv *, jclass);

#ifdef __cplusplus
}
#endif
#endif