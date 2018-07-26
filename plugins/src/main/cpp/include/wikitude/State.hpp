//
//  State.hpp
//  WikitudeUniversalSDK
//
//  Created by Andreas Schacherbauer on 08.11.17.
//  Copyright © 2017 Wikitude. All rights reserved.
//

#ifndef State_hpp
#define State_hpp

#ifdef __cplusplus

#include <map>
#include <vector>
#include <string>

#include "Unit.h"
#include "Geometry.hpp"
#include "Matrix4.hpp"
#include "Timestamp.hpp"


namespace aramis {
    struct State;
    struct TargetState;
}

namespace wikitude { namespace universal_sdk {

    namespace impl {


        struct CommonProperties {
        public:
            CommonProperties(const sdk::Matrix4& viewMatrix_);
            CommonProperties(aramis::TargetState& targetState_);
            sdk::Matrix4    _matrix;
        };

        struct TargetProperties {
            TargetProperties(aramis::TargetState& targetState_);

            std::string     _name;
            float           _depthFactor;
            bool            _isExtended;
            int             _trackingQuality;
        };

        struct ImageTargetState {
            ImageTargetState(aramis::TargetState& targetState_, sdk::Rectangle<int> targetRectInCameraFrame_);

            CommonProperties    _commonProperties;
            TargetProperties    _targetProperties;
            int             _uniqueId;
            sdk::Size<int>  _size;
            mutable sdk::Millimeter _physicalHeight;
            sdk::Rectangle<int>     _targetAreaInCameraFrame;
        };

        struct ObjectTargetState {
            ObjectTargetState(aramis::TargetState& targetState_);

            CommonProperties    _commonProperties;
            TargetProperties    _targetProperties;
            bool                _valid;
            sdk::Rectangle3D<float> _boundingBox;
        };

        struct InstantTargetState {
            InstantTargetState(const sdk::Matrix4& viewMatrix_, bool valid_);
            InstantTargetState(aramis::TargetState& targetState_);

            CommonProperties    _commonProperties;
            bool                _valid;
        };

        struct ImageState {
        public:
            ImageState();
            ImageState(aramis::State& state_);
            ImageState(aramis::State& state_, std::map<std::string, sdk::Rectangle<int>> targetAreasInCameraFrame_);

            void update(aramis::State& state_, std::map<std::string, sdk::Rectangle<int>> targetAreasInCameraFrame_);

            long                        _processedFrameId = -1;
            sdk::Timestamp              _processedFrameTimestamp;

            std::vector<ImageTargetState>      _targetStates;
        };
        
        struct ObjectState {
        public:
            ObjectState();            
            ObjectState(aramis::State& state_);

            void update(aramis::State& state_);
            
            long                        _processedFrameId = -1;
            sdk::Timestamp              _processedFrameTimestamp;
            
            std::vector<ObjectTargetState>      _targetStates;
        };
        
        struct InstantState {
        public:
            InstantState();
            InstantState(aramis::State& state_);

            void update(aramis::State& state_);
            
            long                        _processedFrameId = -1;
            sdk::Timestamp              _processedFrameTimestamp;
            
            std::vector<InstantTargetState>      _targetStates;
        };
    }
    using impl::ImageTargetState;
    using impl::ObjectTargetState;
    using impl::InstantTargetState;
    using impl::ImageState;
    using impl::InstantState;
    using impl::ObjectState;
}}

#endif /* __cplusplus */

#endif /* State_hpp */
