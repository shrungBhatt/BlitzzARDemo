//
//  DeviceIMUInputPluginModule.hpp
//  WikitudeUniversalSDK
//
//  Created by Andreas Schacherbauer on 02.03.18.
//  Copyright © 2018 Wikitude. All rights reserved.
//

#ifndef DeviceIMUInputPluginModule_hpp
#define DeviceIMUInputPluginModule_hpp

#ifdef __cplusplus

#include <functional>

#include "SensorEvent.hpp"

#include "PluginModule.hpp"


namespace wikitude { namespace sdk {

    namespace impl {


        class DeviceIMUInputPluginModule : public PluginModule {
        public:
            virtual ~DeviceIMUInputPluginModule() {}

            /* Called from the Wikitude SDK */
            void registerNotifyNewSensorEventHandler(std::function<void(sdk::SensorEvent sensorEvent_)>);

        protected:
            std::function<void(sdk::SensorEvent)>   _notifyNewSensorEventHandler;
        };
    }
    using impl::DeviceIMUInputPluginModule;
}}

#endif /* __cplusplus */

#endif /* DeviceIMUInputPluginModule_hpp */
