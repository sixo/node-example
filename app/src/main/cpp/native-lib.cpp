/*
 * Copyright (c) 2017, Roman Sisik
 * All rights reserved.
 * See LICENSE for more information.
 */

#include "native-lib.h"
#include <thread>
#include <vector>
#include <node.h>
#include <unistd.h>

void Java_eu_sisik_nodeexample_NodeService_startNode(JNIEnv *env, jobject instance, jobjectArray args)
{
    // Make node's console.log work with Android logging system
    sisik::enableLogging();

    // Prepare fake argv commandline arguments
    auto continuousArray = sisik::makeContinuousArray(env, args);
    auto argv = sisik::getArgv(continuousArray);

    node::Start(argv.size() - 1, argv.data());
}

namespace sisik {
namespace {
    std::thread  logger;
    static int pfd[2];
}

void enableLogging()
{
    redirectStreamsToPipe();
    startLoggingFromPipe();
}

void redirectStreamsToPipe()
{
    setvbuf(stdout, 0, _IOLBF, 0);
    setvbuf(stderr, 0, _IONBF, 0);

    pipe(pfd);
    dup2(pfd[1], 1);
    dup2(pfd[1], 2);
}

void startLoggingFromPipe()
{
    logger = std::thread([](int* pipefd){
        char buf[128];
        std::size_t nBytes = 0;
        while((nBytes = read(pfd[0], buf, sizeof buf - 1)) > 0) {
            if(buf[nBytes - 1] == '\n') --nBytes;
            buf[nBytes] = 0;
            LOGD("%s", buf);
        }
    }, pfd);

    logger.detach();
}

std::vector<char> makeContinuousArray(JNIEnv *env, jobjectArray fromArgs)
{
    int count = env->GetArrayLength(fromArgs);
    std::vector<char> buffer;
    for (int i = 0; i < count; i++)
    {
        jstring str = (jstring)env->GetObjectArrayElement(fromArgs, i);
        const char* sptr = env->GetStringUTFChars(str, 0);

        do {
            buffer.push_back(*sptr);
        }
        while(*sptr++ != '\0');
    }

    return buffer;
}

std::vector<char*> getArgv(std::vector<char>& fromContinuousArray)
{
    std::vector<char*> argv;

    argv.push_back(fromContinuousArray.data());
    for (int i = 0; i < fromContinuousArray.size() - 1; i++)
        if (fromContinuousArray[i] == '\0') argv.push_back(&fromContinuousArray[i+1]);

    argv.push_back(nullptr);

    return argv;
}
} // namespace sisik