#include <jni.h>
#include <android/log.h>

#include <string>
#include <string.h>
#include <unistd.h>
#include "inneropendir.h"
#include <sys/time.h>
#include <sys/stat.h>
#include <sys/types.h>
#include "fcntl.h"
#include <dirent.h>

#ifdef __cplusplus
extern "C"
{
#endif

#define EBUSY_RETRY_TIMES       4
	/// 重试间隔时长(单位：微秒)
#define EBUSY_RETRY_PERIOD      10000UL

#define LOGD(...) ((void)__android_log_print(ANDROID_LOG_DEBUG, "mao_jni", __VA_ARGS__))
#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, "mao_jni", __VA_ARGS__))
#define LOGW(...) ((void)__android_log_print(ANDROID_LOG_WARN, "mao_jni", __VA_ARGS__))
#define LOGE(...) ((void)__android_log_print(ANDROID_LOG_ERROR, "mao_jni", __VA_ARGS__))

	void callbackJava(JNIEnv *env, int times);
	long long getFileSizeByLstat(const char* pathname);

	JNIEXPORT void JNICALL Java_com_ichangmao_jni_MaoJni_test(JNIEnv* env, jobject thiz, jint num)
	{
		callbackJava(env, num);
	}

	void callbackJava(JNIEnv *env, const int times)
	{
		jclass cbClass = env->FindClass("com/ichangmao/jni/Callback");
		jmethodID midCbInit = env->GetMethodID(cbClass, "<init>", "(Ljava/lang/String;)V");
		jobject mCallbackObj = env->NewObject(cbClass, midCbInit, env->NewStringUTF("vanq yang"));
		jmethodID mMidCallback = env->GetMethodID(cbClass, "callback", "(I)V");

		struct timeval tvStart;
		struct timeval tvEnd;
		struct timezone tz;
		gettimeofday(&tvStart, &tz);
		int i;
		for (i = 0; i < times; i++) {
			env->CallVoidMethod(mCallbackObj, mMidCallback, i);
		}

		gettimeofday(&tvEnd, &tz);
		LOGI("start:%ld,end:%ld", tvStart.tv_usec, tvEnd.tv_usec);
		LOGI("callback %d tims,spend %ld us", times, (tvEnd.tv_usec - tvStart.tv_usec));
	}

	JNIEXPORT long long JNICALL Java_com_ichangmao_jni_MaoJni_getFileSize(JNIEnv* env, jobject thiz, jstring path)
	{
		const char* pathname = env->GetStringUTFChars(path, 0);
		return getFileSizeByLstat(pathname);
	}

	long long getFileSizeByLstat(const char* pathname) {
		struct stat file_stat = { 0 };
		if (0 != lstat(pathname, &file_stat)) {
			LOGE("get file size error:%s", pathname);
			return -1;
		}
		return file_stat.st_size;
	}

	int calcFolderSize(int dirfd, const char *dirname, const char *filename, long long llResult[3])
	{
		if (filename == NULL)
		{
			struct INNERDIR *pNowDir = inneropendir(dirname);
			if (pNowDir == NULL)
			{
				LOGE("open dir error:%s", dirname);
				return -1;
			}
			++llResult[1];

			struct dirent* pDirEnt = NULL;
			while ((pDirEnt = innerreaddir(pNowDir)) != NULL)
			{

				if (DT_DIR == pDirEnt->d_type || DT_UNKNOWN == pDirEnt->d_type) {
					if (0 == strcmp(".", pDirEnt->d_name) ||
						0 == strcmp("..", pDirEnt->d_name))
					{
						continue;
					}
				}
				calcFolderSize(dirfd, dirname, pDirEnt->d_name, llResult);
			}
			return 0;
		}

		struct stat file_stat;
		if (fstatat(dirfd, filename, &file_stat, AT_SYMLINK_NOFOLLOW) != 0)
		{
			LOGE("fstatat error. %s", filename);
			return 0;
		}
		if (!S_ISDIR(file_stat.st_mode)) {
			llResult[0] += file_stat.st_size;
			++llResult[2];
			return 0;
		}

		++llResult[1];

		std::string pathname(dirname);
		pathname.append("/");
		pathname.append(filename);
		struct INNERDIR *pNowDir = inneropendir(pathname.c_str());
		if (pNowDir == NULL)
		{
			LOGE("open dir error:%s", pathname.c_str());
			return -1;
		}

		struct dirent* pDirEnt = NULL;
		while ((pDirEnt = innerreaddir(pNowDir)) != NULL)
		{
			std::string subFileName(filename);
			subFileName.append("/");
			subFileName.append(pDirEnt->d_name);
			if (0 == strcmp(".", pDirEnt->d_name) ||
				0 == strcmp("..", pDirEnt->d_name))
			{
				continue;
			}

			calcFolderSize(dirfd, dirname, subFileName.c_str(), llResult);
		}

		innerclosedir(pNowDir);
		pNowDir = NULL;
		return 0;
	}

	int calcFolderSizeByLstat(const char *pathname, long long llResult[3])
	{
		struct stat file_stat = { 0 };
		if (0 != lstat(pathname, &file_stat)) {
			LOGE("lstat error:%s", pathname);
			return -1;
		}
		if (!S_ISDIR(file_stat.st_mode)) {
			llResult[0] += file_stat.st_size;
			++llResult[2];
			return 0;
		}

		++llResult[1];

		struct INNERDIR *pNowDir = inneropendir(pathname);
		if (pNowDir == NULL)
		{
			LOGE("open dir error:%s", pathname);
			return -1;
		}

		struct dirent* pDirEnt = NULL;
		while ((pDirEnt = innerreaddir(pNowDir)) != NULL)
		{
			std::string subFileName(pathname);
			subFileName.append("/");
			subFileName.append(pDirEnt->d_name);
			if (0 == strcmp(".", pDirEnt->d_name) ||
				0 == strcmp("..", pDirEnt->d_name))
			{
				continue;
			}
			calcFolderSizeByLstat(subFileName.c_str(), llResult);
		}

		innerclosedir(pNowDir);
		pNowDir = NULL;
		return 0;
	}

	JNIEXPORT void JNICALL Java_com_ichangmao_jni_MaoJni_calcFileSize(JNIEnv* env, jclass thiz, jstring jstrPath, jlongArray jlaResult, jint calcType)
	{
		const char* pathname;
		long long llResult[3] = { 0 };

		pathname = env->GetStringUTFChars(jstrPath, 0);
		if (pathname == NULL)
		{
			LOGE("path name is null");
			return;
		}
		LOGI("calcType:%d, pathname:%s", calcType, pathname);
		if (env->GetArrayLength(jlaResult) < 3)
		{
			LOGE("param error");
			return;
		}


		if (calcType == 2) {
			calcFolderSizeByLstat(pathname, llResult);
		}
		else
		{
			int dirfd;
			dirfd = open(pathname, O_RDONLY);
			if (0 == dirfd)
			{
				LOGE("open file error");
				return;
			}
			calcFolderSize(dirfd, pathname, NULL, llResult);
		}

		//LOGI("RESULT:%lld,%lld,%lld", llResult[0], llResult[1], llResult[2]);

		env->SetLongArrayRegion(jlaResult, 0, 3, llResult);
	}

#ifdef __cplusplus
}
#endif