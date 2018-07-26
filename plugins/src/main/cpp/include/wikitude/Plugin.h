//
// Created by simon on 08/04/15.
// Copyright (c) 2015 Wikitude. All rights reserved.
//


#ifndef PLUGIN_H_
#define PLUGIN_H_

#ifdef __cplusplus

#include <set>
#include <list>
#include <string>
#include <memory>
#include <unordered_map>
#include <bitset>

#include "Geometry.hpp"
#include "RenderingAPI.hpp"

#include "RecognizedTargetsBucket.hpp"
#include "CameraFrameInputPluginModule.hpp"
#include "DeviceIMUInputPluginModule.hpp"
#include "ImageTrackingPluginModule.hpp"
#include "ObjectTrackingPluginModule.hpp"
#include "InstantTrackingPluginModule.hpp"
#if defined(__APPLE__) || defined(ANDROID)
#include "OpenGLESRenderingPluginModule.hpp"
#endif
#ifdef __APPLE__
#include "MetalRenderingPluginModule.hpp"
#endif
#ifdef _WIN32
#include "D3D11RenderingPluginModule.hpp"
#endif


namespace wikitude {
    namespace sdk {
        namespace impl {


            class CameraFrame;
            class ManagedCameraFrame;
            class RuntimeParameters;
            class CameraParameters;
            class TrackingParameters;
            class RenderingParameters;
            class PluginParameterCollection;

            class Plugin {
            public:
                /**
                 * Constructor, pass a unique identifier for each of your plugins.
                 */
                Plugin(std::string identifier_);

                virtual ~Plugin();

                /**
                 * Will be called once after your Plugin was successfully added to the Wikitude Engine. Initialize your plugin here.
                 */
                virtual void initialize(const std::string& temporaryDirectory_, PluginParameterCollection& pluginParameterCollection_);

                /**
                 * Will be called every time the Wikitude Engine pauses.
                 */
                virtual void pause();

                /**
                 * Will be called when the Wikitude Engine starts for the first time and after every pause.
                 *
                 * @param pausedTime_ the duration of the pause in milliseconds
                 */
                virtual void resume(unsigned int pausedTime_);

                /**
                 * Will be called when the Wikitude Engine shuts down.
                 */
                virtual void destroy();

                /**
                 * Will be called every time the Wikitude Enigine receives a new camera frame from the platform camera.
                 *
                 * @param managedCameraFrame_ frame wrapper object which contains the frame data and some metadata about the frame
                 */
                virtual void cameraFrameAvailable(ManagedCameraFrame& managedCameraFrame_) = 0;

                /**
                 * Will be called after every image recognition cycle.
                 *
                 * @param recognizedTargetsBucket_ an object providing access to all targets recognized in this specific frame
                 */
                virtual void update(const RecognizedTargetsBucket& recognizedTargetsBucket_) = 0;

                /**
                 * Returns the identifier of this particular plugin.
                 * Note: This method should not be overriden.
                 */
                virtual const std::string& getIdentifier() const;

                /**
                 * Set's this plugin enabled or not. A enabled plugin receives cameraFrameAvailable, update, startRender and endRender calls.
                 */
                virtual void setEnabled(bool enabled_);

                /**
                 * Return the enabled state of this particular plugin.
                 */
                virtual bool isEnabled() const;

                virtual bool canPerformTrackingOperationsAlongOtherPlugins();
                virtual bool canUpdateMultipleTrackingInterfacesSimultaneously();

                ImageTrackingPluginModule* getImageTrackingPluginModule() const;
                InstantTrackingPluginModule* getInstantTrackingPluginModule() const;
                ObjectTrackingPluginModule* getObjectTrackingPluginModule() const;
                
                CameraFrameInputPluginModule* getCameraFrameInputPluginModule() const;
                DeviceIMUInputPluginModule* getDeviceIMUInpputPluginModule() const;
#if defined(__APPLE__) || defined(ANDROID)
                OpenGLESRenderingPluginModule* getOpenGLESRenderingPluginModule() const;
#endif
#ifdef __APPLE__
                MetalRenderingPluginModule* getMetalRenderingPluginModule() const;
#endif
#ifdef _WIN32
                D3D11RenderingPluginModule* getD3D11RenderingPluginModule() const;
#endif

            protected:
                void setImageTrackingPluginModule(std::unique_ptr<ImageTrackingPluginModule> imageTrackingPluginModule_);
                void setObjectTrackingPluginModule(std::unique_ptr<ObjectTrackingPluginModule> objectTrackingPluginModule_);
                void setInstantTrackingPluginModule(std::unique_ptr<InstantTrackingPluginModule> instantTrackingPluginModule_);

                void setCameraFrameInputPluginModule(std::unique_ptr<CameraFrameInputPluginModule> cameraFrameInputPluginModule_);
                void setDeviceIMUInputPluginModule(std::unique_ptr<DeviceIMUInputPluginModule> deviceIMUInputPluginModule_);
#if defined(__APPLE__) || defined(ANDROID)
                void setOpenGLESRenderingPluginModule(std::unique_ptr<OpenGLESRenderingPluginModule> openGLESRenderingPluginModule_);
#endif
#ifdef __APPLE__
                void setMetalRenderingPluginModule(std::unique_ptr<MetalRenderingPluginModule> metalRenderingPluginModule_);
#endif
#ifdef _WIN32
                void setD3D11RenderingPluginModule(std::unique_ptr<D3D11RenderingPluginModule> d3d11RenderingPluginModule_);
#endif

                void iterateEnabledPluginModules(std::function<void(PluginModule& activePluginModule_)> activePluginModuleIteratorHandle_);

            protected:
                std::string     _identifier;
                bool            _enabled;

            private:
                std::unique_ptr<ImageTrackingPluginModule>      _imageTrackingModule;
                std::unique_ptr<InstantTrackingPluginModule>    _instantTrackingModule;
                std::unique_ptr<ObjectTrackingPluginModule>     _objectTrackingModule;
                
                std::unique_ptr<CameraFrameInputPluginModule>   _cameraFrameInputModule;
                std::unique_ptr<DeviceIMUInputPluginModule>     _deviceIMUInputPluginModule;
#if defined(__APPLE__) || defined(ANDROID)
                std::unique_ptr<OpenGLESRenderingPluginModule>  _openGlesRenderingModule;
#endif
#ifdef __APPLE__
                std::unique_ptr<MetalRenderingPluginModule>     _metalRenderingModule;
#endif
#ifdef _WIN32
                std::unique_ptr<D3D11RenderingPluginModule>     _d3d11RenderingModule;
#endif
                mutable std::mutex          _pluginModuleAccessMutex;
                std::set<PluginModule*>     _availablePluginModules;
            };
        }
        using impl::PluginParameterCollection;        
        using impl::Plugin;
    }
}

#endif /* __cplusplus */

#endif //__Plugin_H_
