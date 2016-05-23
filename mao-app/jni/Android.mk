LOCAL_PATH := $(call my-dir)
LOCAL_CPP_EXTENSION := .cpp

include $(CLEAR_VARS)

LOCAL_LDLIBS += -llog

LOCAL_MODULE	:= mao-jni
LOCAL_SRC_FILES	:= \
inneropendir.cpp \
mao_jni.cpp

include $(BUILD_SHARED_LIBRARY)