/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_wynntils_antiope_manager_overlay_OverlayManager */

#ifndef _Included_com_wynntils_antiope_manager_overlay_OverlayManager
#define _Included_com_wynntils_antiope_manager_overlay_OverlayManager
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_wynntils_antiope_manager_overlay_OverlayManager
 * Method:    isEnabled
 * Signature: (J)Z
 */
JNIEXPORT jboolean JNICALL Java_com_wynntils_antiope_manager_overlay_OverlayManager_isEnabled
  (JNIEnv *, jobject, jlong);

/*
 * Class:     com_wynntils_antiope_manager_overlay_OverlayManager
 * Method:    isLocked
 * Signature: (J)Z
 */
JNIEXPORT jboolean JNICALL Java_com_wynntils_antiope_manager_overlay_OverlayManager_isLocked
  (JNIEnv *, jobject, jlong);

/*
 * Class:     com_wynntils_antiope_manager_overlay_OverlayManager
 * Method:    setLocked
 * Signature: (JZLjava/util/function/Consumer;)V
 */
JNIEXPORT void JNICALL Java_com_wynntils_antiope_manager_overlay_OverlayManager_setLocked
  (JNIEnv *, jobject, jlong, jboolean, jobject);

/*
 * Class:     com_wynntils_antiope_manager_overlay_OverlayManager
 * Method:    openActivityInvite
 * Signature: (JILjava/util/function/Consumer;)V
 */
JNIEXPORT void JNICALL Java_com_wynntils_antiope_manager_overlay_OverlayManager_openActivityInvite
  (JNIEnv *, jobject, jlong, jint, jobject);

/*
 * Class:     com_wynntils_antiope_manager_overlay_OverlayManager
 * Method:    openGuildInvite
 * Signature: (JLjava/lang/String;Ljava/util/function/Consumer;)V
 */
JNIEXPORT void JNICALL Java_com_wynntils_antiope_manager_overlay_OverlayManager_openGuildInvite
  (JNIEnv *, jobject, jlong, jstring, jobject);

/*
 * Class:     com_wynntils_antiope_manager_overlay_OverlayManager
 * Method:    openVoiceSettings
 * Signature: (JLjava/util/function/Consumer;)V
 */
JNIEXPORT void JNICALL Java_com_wynntils_antiope_manager_overlay_OverlayManager_openVoiceSettings
  (JNIEnv *, jobject, jlong, jobject);

#ifdef __cplusplus
}
#endif
#endif
