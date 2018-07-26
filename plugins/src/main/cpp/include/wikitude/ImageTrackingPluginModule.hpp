//
//  ImageTrackingPluginModule.hpp
//  WikitudeUniversalSDK
//
//  Created by Andreas Schacherbauer on 17.03.18.
//  Copyright © 2018 Wikitude. All rights reserved.
//

#ifndef ImageTrackingPluginModule_hpp
#define ImageTrackingPluginModule_hpp

#ifdef __cplusplus

#include "TrackingPluginModule.hpp"
#include "State.hpp"


namespace wikitude {
    namespace sdk {
        namespace impl {
            class ManagedCameraFrame;
        }
        using impl::ManagedCameraFrame;
    }
}

namespace wikitude { namespace sdk {

    namespace impl {


        class ImageTrackingPluginModule : public TrackingPluginModule {
        public:
            virtual ~ImageTrackingPluginModule() = default;            

            virtual universal_sdk::ImageState getTrackingState() const = 0;
        };
    }
    using impl::ImageTrackingPluginModule;
}}

#endif /* __cplusplus */

#endif /* ImageTrackingPluginModule_hpp */
