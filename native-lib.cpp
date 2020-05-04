#include <jni.h>
#include <string>
#include <opencv2/core.hpp>


extern "C" JNIEXPORT jstring JNICALL
Java_com_example_nativetest_MainActivity_stringFromJNI(JNIEnv* env, jobject)
{
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_example_nativetest_MainActivity_validate(JNIEnv *env, jobject , jlong mad_addr_gr,
                                                  jlong mat_addr_rgba)
{
    cv::Rect();
    cv::Mat();
    std::string hello2="hello from validate";
    return env->NewStringUTF(hello2.c_str());
}

