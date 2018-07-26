#include <jni.h>

#include "jniHelper.h"
#include "Plugin.h"
#include "SimpleInputPlugin.h"


JavaVM* pluginJavaVM;

extern "C" JNIEXPORT jlongArray JNICALL Java_com_wikitude_common_plugins_internal_PluginManagerInternal_createNativePlugins(JNIEnv *env, jobject thisObj, jstring jPluginName) {

    env->GetJavaVM(&pluginJavaVM);
    
    int numberOfPlugins = 1;
    
    jlong cPluginsArray[numberOfPlugins];
    
    JavaStringResource pluginName(env, jPluginName);

    if ( pluginName.str == "simple_input_plugin" ) {
        cPluginsArray[0] = (jlong) new SimpleInputPlugin();
    }
    
    jlongArray jPluginsArray = env->NewLongArray(numberOfPlugins);
    if (jPluginsArray != nullptr) {
        env->SetLongArrayRegion(jPluginsArray, 0, numberOfPlugins, cPluginsArray);
    }
    
    return jPluginsArray;
}
