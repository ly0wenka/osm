#include <jni.h>
#include <sys/mman.h>

extern "C" JNIEXPORT jlong JNICALL
Java_com_example_l5memory_MemoryManager_mmapAllocate(JNIEnv* env, jobject thiz, jint size) {
    // Виділяємо блок пам'яті безпосередньо у ядра Linux
    void* ptr = mmap(NULL, size, PROT_READ | PROT_WRITE, MAP_PRIVATE | MAP_ANONYMOUS, -1, 0);
    return (jlong)ptr;
}