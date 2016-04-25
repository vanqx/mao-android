#include <jni.h>
#include <android/log.h>

#include<sys/time.h>

#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, "mao_jni", __VA_ARGS__))
#define LOGW(...) ((void)__android_log_print(ANDROID_LOG_WARN, "mao_jni", __VA_ARGS__))

void callbackJava(JNIEnv *env, int times);

JNIEXPORT void JNICALL Java_com_ichangmao_jni_MaoJni_test(JNIEnv* env, jobject thiz,jint num)
{
	callbackJava(env, num);
}

void callbackJava(JNIEnv *env, const int times)
{
	jclass cbClass = (*env)->FindClass(env,"com/ichangmao/jni/Callback");
	jmethodID midCbInit = (*env)->GetMethodID(env, cbClass, "<init>", "(Ljava/lang/String;)V");
	jobject mCallbackObj = (*env)->NewObject(env, cbClass, midCbInit, (*env)->NewStringUTF(env, "vanq yang"));
	jmethodID mMidCallback = (*env)->GetMethodID(env, cbClass, "callback", "(I)V");

	struct timeval tvStart;
	struct timeval tvEnd;
	struct timezone tz;
	gettimeofday(&tvStart, &tz);
	int i;
	for (i = 0; i < times; i++) {
		(*env)->CallVoidMethod(env, mCallbackObj, mMidCallback, i);
	}

	gettimeofday(&tvEnd, &tz);
	LOGI("start:%ld,end:%ld", tvStart.tv_usec, tvEnd.tv_usec);
	LOGI("callback %d tims,spend %ld us", times, (tvEnd.tv_usec - tvStart.tv_usec));
}