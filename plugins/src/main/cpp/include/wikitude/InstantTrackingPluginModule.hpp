//
//  InstantTrackingPluginModule.hpp
//  WikitudeUniversalSDK
//
//  Created by Andreas Schacherbauer on 28.02.18.
//  Copyright © 2018 Wikitude. All rights reserved.
//

#ifndef InstantTrackingPluginModule_hpp
#define InstantTrackingPluginModule_hpp

#ifdef __cplusplus

#include "ErrorHandling.hpp"
#include "Geometry.hpp"
#include "ManagedCameraFrame.hpp"

#include "TrackingPluginModule.hpp"
#include "State.hpp"
#include "SensorEvent.hpp"
#include "InstantTrackingState.hpp"


namespace wikitude { namespace sdk {

    namespace impl {


        class InstantTrackingPluginModule : public TrackingPluginModule {
        public:
            virtual ~InstantTrackingPluginModule() {}
            
            virtual void sensorEventAvailable(const sdk::SensorEvent& event_) = 0;

            /**
             * Is called to check if the state change, that was requested from the user, can actually be performed
             */
//            NO_DISCARD virtual sdk::CallStatus changeInstantTrackingState(InstantTrackingState requestedInstantTrackingState_) = 0;
//            NO_DISCARD virtual sdk::CallStatus canStartTracking() = 0;

            virtual universal_sdk::InstantState getInitializationState(/*float deviceHeightAboveGround*/) const = 0;
            virtual universal_sdk::InstantState getTrackingState() const = 0;

//            virtual void requestCurrentPointCloud(std::function<void(const std::vector<Point3D<float>>& pointCloud_)> pointCloudHandler_) = 0;
//            virtual void convertScreenCoordinate(Point<float> screenCoordinate_, std::function<void(Point3D<float> pointCloudCoordinate_)> completionHandler_, std::function<void(const Error& error_)> errorHandler_) = 0;
        };
    }
    using impl::InstantTrackingPluginModule;
}}

#endif /* __cplusplus */

#endif /* InstantTrackingPluginModule_hpp */
