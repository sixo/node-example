/*
 * Copyright (c) 2017, Roman Sisik
 * All rights reserved.
 * See LICENSE for more information.
 */

#ifndef NODEEXAMPLE_NATIVE_LIB_H
#define NODEEXAMPLE_NATIVE_LIB_H

#include <jni.h>
#include <android/log.h>
#include <vector>

#define LOGD(...) (__android_log_print(ANDROID_LOG_DEBUG, "Node.js", __VA_ARGS__))

extern "C" {
JNIEXPORT void JNICALL
Java_eu_sisik_nodeexample_NodeService_startNode(JNIEnv *env, jobject instance,
                                                 jobjectArray args);
JNIEXPORT void JNICALL
Java_eu_sisik_nodeexample_NodeService_stopNode(JNIEnv *env, jobject instance);
}

namespace sisik {
    void enableLogging();
    void redirectStreamsToPipe();
    void startLoggingFromPipe();
    std::vector<char> makeContinuousArray(JNIEnv *env, jobjectArray fromArgs);
    std::vector<char*> getArgv(std::vector<char>& fromContinuousArray);
}
#endif