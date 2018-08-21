
#include <jni.h>
#include <stdio.h>
#include <string.h>

#ifndef __x86_64__

/**
 * 测试内联汇编，分别根据AArch32架构以及AArch64架构来实现一个简单的减法计算
 * @param a 被减数
 * @param b 减数
 * @return 减法得到的差值
 */
static int __attribute__((naked, pure)) MyASMTest(int a, int b)
{
#ifdef __arm__

    asm(".thumb");
    asm(".syntax unified");

    asm("sub r0, r0, r1");
    asm("add r0, r0, #1");  // 为了区分当前用的是AArch32还是AArch64，这里对于AArch32情况下再加1
    asm("bx lr");

#else

    asm("sub w0, w0, w1");
    asm("ret");

#endif
}

#else

extern int MyASMTest(int a, int b);

#endif

JNICALL jstring Java_com_threestudio_multi_1downloader_HomeActivity_stringFromJNI
        (JNIEnv *env, jobject this)
{
    char strBuf[128];

    sprintf(strBuf, "Just Test For JNI C");

    return (*env)->NewStringUTF(env, strBuf);
}
