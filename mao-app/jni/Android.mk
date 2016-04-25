LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_LDLIBS += -llog

LOCAL_MODULE	:= mao-jni
LOCAL_SRC_FILES	:= mao_jni.c

include $(BUILD_SHARED_LIBRARY)