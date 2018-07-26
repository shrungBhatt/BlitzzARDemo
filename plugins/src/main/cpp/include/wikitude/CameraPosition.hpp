//
//  CameraPosition.hpp
//  CommonLibrary
//
//  Created by Andreas Schacherbauer on 19.10.17.
//  Copyright © 2017 Wikitude. All rights reserved.
//

#ifndef FrameProviderPosition_hpp
#define FrameProviderPosition_hpp

#ifdef __cplusplus

namespace wikitude { namespace sdk {

    namespace impl {

        /** @enum CameraPosition
         *  @brief An enum indicating the physical position of the camera used to capture frames.
         */
        enum class CameraPosition : int {
            /** @brief Indicates that the camera position is undefined. Should be used for desktop web cams.
             */
            Unspecified,
            /** @brief Indicates that the camera is located on the back.
             */
            Back,
            /** @brief Indicates that the camera is located on the back.
             */
            Front
        };
    }
    using impl::CameraPosition;
}}

#endif /* __cplusplus */

#endif /* FrameProviderPosition_hpp */
