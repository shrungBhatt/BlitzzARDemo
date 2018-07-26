//
//  Frame.h  
//
//  Copyright (c) 2015 Wikitude. All rights reserved.
//

#ifndef FRAME_H
#define FRAME_H

#ifdef __cplusplus

#include <cstdint>

#include "Geometry.hpp"
#include "ColorSpace.hpp"


namespace wikitude { namespace sdk {
    
    namespace impl {

        class FrameStrides {
        public:

            FrameStrides(int luminancePixelStride_ = 0 , int luminanceRowStride_ = 0, int chrominanceRedPixelStride_ = 0, int chrominanceRedRowStride_ = 0, int chrominanceBluePixelStride_ = 0, int chrominanceBlueRowStride_ = 0);
            virtual ~FrameStrides();

            int getLuminancePixelStride() const;
            int getLuminanceRowStride() const;
            int getChrominanceRedPixelStride() const;
            int getChrominanceRedRowStride() const;
            int getChrominanceBluePixelStride() const;
            int getChrominanceBlueRowStride() const;

        private:
            int _luminancePixelStride;
            int _luminanceRowStride;
            int _chrominanceRedPixelStride;
            int _chrominanceRedRowStride;
            int _chrominanceBluePixelStride;
            int _chrominanceBlueRowStride;
        };

        class PluginFrame {
        public:
            PluginFrame(ColorSpace frameColorSpace_, unsigned char* frameData_, int frameDataSize_, Size<int> const size_, float scaledWidth_, float scaledHeight_, std::int64_t presentationTimestampValue_, std::int32_t presentationTimestampTimescale_, FrameStrides frameStrides_, bool hasFrameStrides_);
            virtual ~PluginFrame();
            
            /**
             * Specifies the frame color space. Depending on the color space, frame data and data size differs.
             *
             * @return FrameColorSpace
             */
            ColorSpace getFrameColorSpace() const;

            /**
             *
             *
             * @return data pointer to the frames
             */
            const unsigned char* getData() const;
            int getFrameDataSize() const;

            const Size<int>& getSize() const;

            float getScaledWidth() const;
            float getScaledHeight() const;

            std::int64_t getPresentationTimestampValue() const;
            std::int32_t getPresentationTimestampTimescale() const;

            const FrameStrides getFrameStrides() const;
            bool hasFrameStrides() const;
            
        private:
            ColorSpace          _frameColorSpace;
            FrameStrides        _frameStrides;
            unsigned char*      _frameData;
            int                 _frameDataSize;
            Size<int>           _size;
            float               _scaledWidth;
            float               _scaledHeight;
            std::int64_t        _presentationTimestampValue;
            std::int32_t        _presentationTimestampTimescale;
            bool                _hasFrameStrides;
        };
    }
    using impl::PluginFrame;
    using impl::FrameStrides;
}}

#endif /* __cplusplus */

#endif
