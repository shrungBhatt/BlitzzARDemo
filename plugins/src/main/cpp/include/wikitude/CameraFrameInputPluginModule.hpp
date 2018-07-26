//
//  CameraFrameInputPluginModule.hpp
//  WikitudeUniversalSDK
//
//  Created by Andreas Schacherbauer on 28.02.18.
//  Copyright © 2018 Wikitude. All rights reserved.
//

#ifndef CameraFrameInputPluginModule_hpp
#define CameraFrameInputPluginModule_hpp

#ifdef __cplusplus

#include <cstdint>
#include <functional>

#include "Error.hpp"
#include "CameraFrame.hpp"

#include "PluginModule.hpp"


namespace wikitude { namespace sdk {
    
    namespace impl {


        class CameraFrameInputPluginModule : public PluginModule {
        public:
            CameraFrameInputPluginModule() noexcept = default;
            virtual ~CameraFrameInputPluginModule() = default;

            /**
             * Override/implement this method to know when the default platform camera is fully released and this camera frame input plugin module can safely access all platform camera resources
             */
            virtual void onCameraReleased() = 0;
            virtual void onCameraReleaseFailed(const sdk::Error& error_) = 0;

            /**
             * Default: false
             */
            bool requestsCameraFrameRendering();

            /* Called from the Wikitude SDK */
            void registerOnPluginCameraReleasedHandler(std::function<void()> onPluginCameraReleasedHandler_);
            void registerNotifyNewUnmanagedCameraFrameHandler(std::function<void(const sdk::CameraFrame& cameraFrame_)> notifyNewUnmanagedCameraFrameHandler_);
            void registerCameraToSurfaceAngleChangedHandler(std::function<void(float cameraToSurfaceAngle_)> cameraToSurfaceAngleChangedHandler_);

        protected:
            /**
             * Call this method to notify a new camera frame to the SDK
             */
            void notifyNewUnmanagedCameraFrameToSDK(const sdk::CameraFrame& cameraFrame_);
            
            /**
             * Call this method to notify the SDK that this camer frame input plugin module fully released all platform camera resources.
             * 
             */
            void notifyPluginCameraReleased();

            void setCameraToSurfaceAngle(float cameraToSurfaceAngle_);

        protected:
            bool                                    _requestsCameraFrameRendering = false;
            
        private:
            std::function<void()>                           _onPluginCameraReleasedHandler;
            std::function<void(const sdk::CameraFrame&)>    _notifyNewUnmanagedCameraFrameHandler;
            std::function<void(float)>                      _cameraToSurfaceAngleChangedHandler;
        };
    }
    using impl::CameraFrameInputPluginModule;
}}

#endif /* __cplusplus */

#endif /* CameraFrameInputPluginModule_hpp */
